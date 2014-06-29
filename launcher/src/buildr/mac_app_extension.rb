
# TODO: Eventually extract this module to an external project.

module MacAppBundle

  include Extension

  class PlistBuilder
    def initialize(target)
      @target = target
    end

    def build(hash)
      xm = Builder::XmlMarkup.new(target: @target, indent: 2)
      xm.instruct!
      xm.declare! :DOCTYPE, :plist, :PUBLIC, '-//Apple//DTD PLIST 1.0//EN', 'http://www.apple.com/DTDs/PropertyList-1.0.dtd'
      xm.plist(version: '1.0') do
        recurse xm, hash
      end
    end

  protected

    def recurse(xm, obj)
      if obj.is_a?(String)
        xm.string obj
      elsif obj.is_a?(Array)
        xm.array do
          obj.each do |value|
            recurse xm, value
          end
        end
      elsif obj.is_a?(Hash)
        xm.dict do
          obj.each_pair do |key, value|
            xm.key key.to_s
            recurse xm, value
          end
        end
      else
        # Float   => <real>
        # Integer => <integer>
        # Time?   => <date>
        # Boolean => <true/> or <false/>
        # ???     => <data>
        raise "Not supported yet: #{obj} type: #{obj.class}"
      end
    end
  end

  class MacAppBundleTask < Rake::Task
    # The human-readable name of the app.
    attr_accessor :app_name

    # Unique Java package-like ID identifying the bundle.
    attr_accessor :bundle_identifier

    # The main class to run the application, dot-separated.
    attr_accessor :main_class

    # The version of the JRE to bundle (e.g. '1.8.0_05'.)
    # Optional. If not specified, it will use the system JRE.
    attr_accessor :bundle_jre

    # Identity to use for signing the app.
    attr_accessor :signing_identity

    def initialize(*args)
      super

      enhance do |task|
        [:app_name, :bundle_identifier, :main_class, :signing_identity].each do |attr|
          raise "#{attr} not specified" unless send(attr)
        end

        puts "Creating #{app_name}.app"

        # Clean out any partial results from the previous run.
        #TODO: Apple's tools to build apps seem to copy only what has been modified, so that would be nice.
        app = @project.path_to("target/#{app_name}.app")
        FileUtils.rm_rf(app) if File.exists?(app)

        # Stub containing a ton of static files.
        #TODO: These would all move to the external project.
        Buildr::Filter.new.
          from('launcher/src/mac/app-bundle-stub').
          into("#{app}/").
          run

        FileUtils.mkdir_p("#{app}/Contents/Java/")
        FileUtils.cp(@project.package(:jar).to_s, "#{app}/Contents/Java/")

        copy_jre_files(bundle_jre, app) if bundle_jre

        File.open("#{app}/Contents/Info.plist", 'w') do |io|
          dict = {
            CFBundleDevelopmentRegion:      'English',
            CFBundleExecutable:             app_name,
            CFBundleInfoDictionaryVersion:  '6.0',
            CFBundleName:                   app_name,
            CFBundlePackageType:            'APPL',
            CFBundleIdentifier:             bundle_identifier,
            CFBundleSignature:              '????',
            CFBundleGetInfoString:          "#{app_name} #{@project.version} - #{@project.manifest['Copyright']}",
            CFBundleShortVersionString:     @project.version,
            CFBundleVersion:                @project.version,
            NSHumanReadableCopyright:       @project.manifest['Copyright'],
            JVMArchs:                       ['x86_64'],
            JVMMainClassName:               main_class,
            JVMOptions:                     ['-ea', "-Xdock:name=#{app_name}"]
          }
          dict[:JVMRuntime] = "jdk#{bundle_jre}.jdk" if bundle_jre
          PlistBuilder.new(io).build(dict)
        end

        # TODO: Program icon for Contents/Resources/Hex.icns :-(

        codesign app
      end
    end

  protected

    # @param version [String] the version of the JRE ('1.8.0_05')
    # @param app [String] the base dir of the app.
    def copy_jre_files(version, app)
      # You want to be sure the JRE you bundle is the one you configured.
      java_root = File.dirname(File.dirname(File.realpath(ENV['JAVA_HOME'])))
      basename = File.basename(java_root)
      if "jdk#{version}.jdk" != basename
        raise "bundle_jre specifies version (#{version}) but we are running against (#{basename}). Change JAVA_HOME."
      end

      # If I extract this plugin then others might want to specify which exclusions apply.
      # Here is how I classified it:

      unused_tool_files = %w{
        bin/rmid
        bin/rmiregistry
        bin/tnameserv
        bin/keytool
        bin/policytool
        bin/orbd
        bin/servertool
      }

      java_webstart_files = %w{
        bin/javaws
        lib/javaws.jar
      }

      flight_recorder_files = %w{
        lib/jfr
        lib/jfr/*
        lib/jfr.jar
      }

      javafx_files = %w{
        THIRDPARTYLICENSEREADME-JAVAFX.txt 
        lib/javafx.properties
        lib/jfxswt.jar
        lib/libdecora_sse.dylib   
        lib/libfxplugins.dylib
        lib/libglass.dylib
        lib/libglib-lite.dylib
        lib/libgstreamer-lite.dylib
        lib/libjavafx_font.dylib
        lib/libjavafx_font_t2k.dylib
        lib/libjavafx_iio.dylib
        lib/libjfxmedia.dylib
        lib/libjfxwebkit.dylib
        lib/libprism_common.dylib   
        lib/libprism_es2.dylib
        lib/libprism_sw.dylib        
      }

      excluded_files = unused_tool_files + java_webstart_files + flight_recorder_files + javafx_files

      plugin = "#{app}/Contents/PlugIns/jdk#{version}.jdk"

      #TODO: Is there a way to do these two copies in one step without the exclusions all having huge prefixes?

      # JRE files
      Buildr::Filter.new.
        from("#{ENV['JAVA_HOME']}/jre").
        into("#{plugin}/Contents/Home/jre").
        exclude(excluded_files).
        run

      # Files outside the jre required to make things work.
      Buildr::Filter.new.
        from("#{java_root}").
        into(plugin).
        include('Contents/Info.plist').
        include('Contents/MacOS/libjli.dylib').
        run

      codesign(plugin)
    end

    def codesign(dir)
      puts "Signing #{File.basename(dir)}"
      system "codesign -s \"#{signing_identity}\" --timestamp \"#{dir}\""
    end

    def associate_with(project)
      @project = project
    end
  end


  first_time do
    desc 'Create a Mac OS X app bundle'
    Project.local_task('mac_app')
  end

  before_define do |project|
    mac_app = MacAppBundleTask.define_task('mac_app')
    mac_app.send :associate_with, project
    project.task(mac_app)
  end

  after_define do |project|
    task('mac_app' => project.package(:jar))
  end

  def mac_app
    task('mac_app')
  end
end

class Buildr::Project
  include MacAppBundle
end
