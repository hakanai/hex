require_relative 'launcher/src/buildr/mac_app_extension'

VERSION_NUMBER = '0.4'
COPYRIGHT = "Copyright \u00A9 2009-2014  Hex Project"

if ENV['JAVA_HOME_8']
  ENV['JAVA_HOME'] = ENV['JAVA_HOME_8']
end

repositories.remote << 'https://oss.sonatype.org/content/repositories/releases'
repositories.remote << 'https://oss.sonatype.org/content/repositories/snapshots'
repositories.remote << 'https://www.ibiblio.org/maven2/'
repositories.remote << 'https://mirrors.ibiblio.org/pub/mirrors/maven2/'
repositories.remote << 'https://repo1.maven.org/maven2/'

if ENV['DEPLOY_USER']
  repositories.release_to = { url: ( VERSION_NUMBER =~ /SNAPSHOT/ ?
                                     'https://oss.sonatype.org/content/repositories/snapshots' :
                                     'https://oss.sonatype.org/service/local/staging/deploy/maven2' ),
                              username: ENV['DEPLOY_USER'],
                              password: ENV['DEPLOY_PASS'] }
end

INTELLIJ_ANNOTATIONS  =   artifact('com.intellij:annotations:jar:12.0')
SWINGX                = [ artifact('org.swinglabs.swingx:swingx-action:jar:1.6.6-SNAPSHOT'),
                          artifact('org.swinglabs.swingx:swingx-common:jar:1.6.6-SNAPSHOT'),
                          artifact('org.swinglabs.swingx:swingx-core:jar:1.6.6-SNAPSHOT'),
                          artifact('org.swinglabs.swingx:swingx-painters:jar:1.6.6-SNAPSHOT'),
                          artifact('org.swinglabs.swingx:swingx-plaf:jar:1.6.6-SNAPSHOT') ]
JRUBY                 =   artifact('org.jruby:jruby-complete:jar:1.7.13')
HEX_COMPONENTS        = [ artifact('org.trypticon.hex:hex-anno:jar:0.6.1-SNAPSHOT'),
                          artifact('org.trypticon.hex:hex-binary:jar:0.6.1-SNAPSHOT'),
                          artifact('org.trypticon.hex:hex-interpreter:jar:0.6.1-SNAPSHOT'),
                          artifact('org.trypticon.hex:hex-viewer:jar:0.6.1-SNAPSHOT'),
                          artifact('org.trypticon.hex:hex-util:jar:0.6.1-SNAPSHOT') ]
GUM                   =   artifact('org.trypticon.gum:gum:jar:0.1')
HAQUA                 =   artifact('org.trypticon.haqua:haqua:jar:0.1')
ICU4J                 = [ artifact('com.ibm.icu:icu4j:jar:68.2'),
                          artifact('com.ibm.icu:icu4j-charset:jar:68.2') ]
SNAKEYAML             =   artifact('org.yaml:snakeyaml:jar:1.13')
R_SYNTAX              =   artifact('com.fifesoft:rsyntaxtextarea:jar:2.5.0')
SYNTAX_PANE           =   artifact('de.sciss:syntaxpane:jar:1.1.2')
MAC_WIDGETS           = [ artifact('com.jtechdev:macwidgets:jar:1.0.1'),
                          artifact('com.jgoodies:jgoodies-forms:jar:1.7.2'),
                          artifact('com.jgoodies:jgoodies-common:jar:1.7.0')]

download artifact('org.swinglabs.swingx:swingx-action:jar:1.6.6-SNAPSHOT') =>
  'https://github.com/trejkaz/swingx/releases/download/v1.6.6-SNAPSHOT.2014.06.15/swingx-action-1.6.6-SNAPSHOT.jar'
download artifact('org.swinglabs.swingx:swingx-common:jar:1.6.6-SNAPSHOT') =>
  'https://github.com/trejkaz/swingx/releases/download/v1.6.6-SNAPSHOT.2014.06.15/swingx-common-1.6.6-SNAPSHOT.jar'
download artifact('org.swinglabs.swingx:swingx-core:jar:1.6.6-SNAPSHOT') =>
  'https://github.com/trejkaz/swingx/releases/download/v1.6.6-SNAPSHOT.2014.06.15/swingx-core-1.6.6-SNAPSHOT.jar'
download artifact('org.swinglabs.swingx:swingx-painters:jar:1.6.6-SNAPSHOT') =>
  'https://github.com/trejkaz/swingx/releases/download/v1.6.6-SNAPSHOT.2014.06.15/swingx-painters-1.6.6-SNAPSHOT.jar'
download artifact('org.swinglabs.swingx:swingx-plaf:jar:1.6.6-SNAPSHOT') =>
  'https://github.com/trejkaz/swingx/releases/download/v1.6.6-SNAPSHOT.2014.06.15/swingx-plaf-1.6.6-SNAPSHOT.jar'
download artifact('com.ibm.icu:icu4j-charsets:jar:53.1') =>
  'http://download.icu-project.org/files/icu4j/53.1/icu4j-charset-53_1.jar'


desc 'Hex'
define 'hex' do
  project.version = VERSION_NUMBER
  project.group = 'org.trypticon.hex'
  manifest['Copyright'] = COPYRIGHT
  compile.options.source = compile.options.target = '1.8'

  desc 'Hex Formats'
  define 'formats' do
    compile.with INTELLIJ_ANNOTATIONS
    compile.with HEX_COMPONENTS, JRUBY
    package :jar
  end

  desc 'Hex Scripting'
  define 'scripting' do
    compile.with INTELLIJ_ANNOTATIONS
    compile.with HEX_COMPONENTS, SWINGX, MAC_WIDGETS, R_SYNTAX
    compile.with projects('formats')
  end

  desc 'Hex Main'
  define 'main' do
    compile.with INTELLIJ_ANNOTATIONS
    compile.with HEX_COMPONENTS, GUM, HAQUA, SWINGX, ICU4J, SNAKEYAML
    compile.with projects('formats')
    test.with project('formats').test.compile.target
    package :jar
  end

  # Executable jar file for the application.
  package(:jar).tap do |pkg|
    %w{formats main}.each do |p|
      pkg.merge project(p)
      pkg.merge project(p).compile.dependencies
    end
    pkg.with manifest: manifest.merge('Main-Class' => 'org.trypticon.hex.gui.Main')
  end

  if RbConfig::CONFIG['host_os'] =~ /darwin|mac os/
    mac_app.app_name = 'Hex'
    mac_app.bundle_identifier = 'org.trypticon.Hex'
    mac_app.main_class = 'org.trypticon.hex.gui.Main'
    mac_app.bundle_jre = '1.8.0_05'
    mac_app.signing_identity = 'Trejkaz'
  end

end
