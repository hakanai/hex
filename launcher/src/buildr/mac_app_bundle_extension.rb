
# TODO: Eventually extract this module to an external project.

module MacAppBundle

  include Buildr::Extension

  class MacAppBundleTask < Rake::Task
    attr_accessor :app_name

    def initialize(*args)
      super

      enhance do |task|
        #TODO: @project is always nil. Why?
        app = @project.path_to("target/#{app_name}.app")
        if File.exists?(app)
          FileUtils.rm_rf(app)
        end
        Dir.mkdir(app)
        #TODO: Path here would be part of the external project.
        FileUtils.cp_r(@project.path_to('launcher/src/mac/app-bundle-stub'), app)
      end

#          <chmod file="build/Hex.app/Contents/MacOS/Hex" perm="ugo+rx"/>
#
#          <copy file="src/mac/Info.plist" todir="build/Hex.app/Contents">
#              <filterset>
#                  <filter token="app.name" value="${app.name}"/>
#                  <filter token="app.version" value="${app.version}"/>
#                  <filter token="app.copyright" value="${app.copyright}"/>
#              </filterset>
#          </copy>
#
#          <!-- TODO: I need an icon file to go into Contents/Resources/Hex.icns :-( -->
#
#          <copy todir="build/Hex.app/Contents/Java">
#              <fileset dir="../formats/build" includes="hex-formats.jar"/>
#              <fileset dir="../main/build" includes="hex-main.jar"/>
#
#              <fileset dir="../lib" includes="hex-anno.jar, hex-binary.jar, hex-interpreter.jar, hex-util.jar,
#                                              hex-viewer.jar, snakeyaml.jar, swingx.jar, icu4j.jar,
#                                              icu4j-charset.jar, jruby-complete.jar"/>
#
#              <!-- Mac-specific -->
#              <fileset dir="../lib" includes="gum.jar, haqua.jar"/>
#          </copy>
#
#          <exec executable="codesign" failonerror="true">
#              <arg value="-s"/>
#              <arg value="${app.signer}"/>
#              <arg value="--timestamp"/>
#              <arg value="build/Hex.app"/>
#          </exec>
#      </target>

    end

    # Extra methods here

  protected

    # Associates this task with project and particular usage (:main, :test).
    def associate_with(project, usage) #:nodoc:
      @project, @usage = project, usage
    end
  end

  before_define do |project|
    mac_app_bundle = MacAppBundleTask.define_task('mac_app_bundle')

    project.task 'mac_app_bundle' do |task|
      #TODO: This never executes. Why?
      mac_app_bundle.send :associate_with, project, :main
      project.local_task('mac_app_bundle')
    end
  end

  after_define do |project|
    task('mac_app_bundle' => project.package(:jar))
  end

  def mac_app_bundle
    task('mac_app_bundle')
  end
end

class Buildr::Project
    include MacAppBundle
end

Project.local_task('mac_app_bundle') do |name|
  puts "Creating Mac OS X app bundle for #{name}"
end

