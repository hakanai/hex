
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

        File.open("#{app}/Contents/Info.plist", 'w') do |io|
          PlistBuilder.new(io).build(
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
            # If you specify this, you have to bundle Java itself. Maybe that isn't a bad idea though. TODO: Decide.
            #JVMRuntime: 'jdk1.7.0_45.jdk',
            JVMArchs:                       ['x86_64'],
            JVMMainClassName:               main_class,
            JVMOptions:                     ['-ea', "-Xdock:name=#{app_name}"]
          )
        end

        # TODO: Program icon for Contents/Resources/Hex.icns :-(

        puts "Signing #{app_name}.app"
        system "codesign -s \"#{signing_identity}\" --timestamp \"#{app}\""
      end
    end

  protected

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
