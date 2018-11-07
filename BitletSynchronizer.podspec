#
# Be sure to run `pod lib lint BitletSynchronizer.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = 'BitletSynchronizer'
  s.version          = '0.3.2'
  s.summary          = 'BitletSynchronizer makes it easy to handle online data for both Android and iOS.'

# This description is used to generate tags and improve search results.
#   * Think: What does it do? Why did you write it? What is the focus?
#   * Try to keep it short, snappy and to the point.
#   * Write the description between the DESC delimiters below.
#   * Finally, don't worry about the indent, CocoaPods strips it!

  s.description      = <<-DESC
BitletSynchronizer is a library to create Bitlet objects, which can be implemented as data models. Together with the synchronizer it will be able to easily fetch the data.
                       DESC

  s.homepage         = 'https://github.com/crescentflare/BitletSynchronizer'
  # s.screenshots     = 'www.example.com/screenshots_1', 'www.example.com/screenshots_2'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'Crescent Flare Apps' => 'info@crescentflare.com' }
  s.source           = { :git => 'https://github.com/crescentflare/BitletSynchronizer.git', :tag => s.version.to_s }
  # s.social_media_url = 'https://twitter.com/<TWITTER_USERNAME>'

  s.ios.deployment_target = '8.0'

  s.source_files = 'BitletSynchronizerIOS/BitletSynchronizer/Classes/**/*'
  
  # s.resource_bundles = {
  #   'BitletSynchronizer' => ['BitletSynchronizer/Assets/*.png']
  # }

  # s.public_header_files = 'Pod/Classes/**/*.h'
  # s.frameworks = 'UIKit', 'MapKit'
  # s.dependency 'AFNetworking', '~> 2.3'
end
