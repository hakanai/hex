
NAME = 'Hex'
VERSION_NUMBER = '0.4'
COPYRIGHT = 'Copyright (C) 2009-2014  Trejkaz, Hex Project'

ENV['JAVA_HOME'] = ENV['JAVA_HOME_8']

repositories.remote << 'http://www.ibiblio.org/maven2/'
repositories.remote << 'http://mirrors.ibiblio.org/pub/mirrors/maven2/'
repositories.remote << 'http://repo1.maven.org/maven2/'

INTELLIJ_ANNOTATIONS  =   artifact('com.intellij:annotations:jar:12.0')
SWINGX                = [ artifact('org.swinglabs.swingx:swingx-core:jar:1.6.4-SNAPSHOT').from('lib/swingx-core-1.6.4-SNAPSHOT.jar'),
                          artifact('org.swinglabs.swingx:swingx-painters:jar:1.6.4-SNAPSHOT').from('lib/swingx-painters-1.6.4-SNAPSHOT.jar'),
                          artifact('org.swinglabs.swingx:swingx-plaf:jar:1.6.4-SNAPSHOT').from('lib/swingx-plaf-1.6.4-SNAPSHOT.jar')]
JRUBY                 =   artifact('org.jruby:jruby-complete:jar:1.7.12')
HEX_COMPONENTS        = [ artifact('org.trypticon.hex:hex-anno:jar:0.6').from('lib/hex-anno-0.6.jar'),
                          artifact('org.trypticon.hex:hex-binary:jar:0.6').from('lib/hex-binary-0.6.jar'),
                          artifact('org.trypticon.hex:hex-interpreter:jar:0.6').from('lib/hex-interpreter-0.6.jar'),
                          artifact('org.trypticon.hex:hex-viewer:jar:0.6').from('lib/hex-viewer-0.6.jar'),
                          artifact('org.trypticon.hex:hex-util:jar:0.6').from('lib/hex-util-0.6.jar') ]
GUM                   =   artifact('org.trypticon.gum:gum:jar:0.1-SNAPSHOT').from('lib/gum-0.1-SNAPSHOT.jar')
HAQUA                 =   artifact('org.trypticon.haqua:haqua:jar:0.1-SNAPSHOT').from('lib/haqua-0.1-SNAPSHOT.jar')
ICU4J                 = [ artifact('com.ibm.icu:icu4j:jar:53.1'),
                          artifact('com.ibm.icu:icu4j-charsets:jar:53.1').from('lib/icu4j-charsets-53.1.jar') ]
SNAKEYAML             =   artifact('org.yaml:snakeyaml:jar:1.13')

desc 'Main project'
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

  desc 'Hex Main'
  define 'main' do
    compile.with INTELLIJ_ANNOTATIONS
    compile.with HEX_COMPONENTS, GUM, HAQUA, SWINGX, ICU4J, SNAKEYAML
    compile.with projects('formats')
    test.with project('formats').test.compile.target
    package :jar
  end

#  binaries_id = "#{id}-components-#{version}"
#  package(:zip, :file => _("target/#{binaries_id}.zip")).path(binaries_id).tap do |path|
#    path.include 'COPYING', 'COPYING.LESSER', 'CHANGELOG', 'README.markdown'
#
#    %w{anno binary interpreter util viewer}.each do |p|
#      path.include project(p).package(:jar)
#    end
#
##    path.include project('anno').packages.map{|p| "#{p}" }
##    path.include project('binary').packages.map{|p| "#{p}" }
##    path.include project('interpreter').packages.map{|p| "#{p}" }
##    path.include project('util').packages.map{|p| "#{p}" }
##    path.include project('viewer').packages.map{|p| "#{p}" }
#
#    path.include 'examples'
#    path.exclude 'examples/examples.iml'
#    path.exclude '**/target', '**/reports'
#  end

end
