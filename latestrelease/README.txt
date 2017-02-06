
Configuration:
	Windows 7
	Java 7 64-bit
	ProM 6.5
	

To install the package:

1. Copy folder 'processriskindicators-6' to ProM packages folder.

2. Add to packages.xml under <installed-packages> entry:

<package name="overallprocessrisk" version="6" os="all" url="http://www.yawlfaundation.org" desc="overallprocessrisk" org="QUT" auto="false" hasPlugins="true" license="LGPL" author="A. Pika" maintainer="A. Pika" logo="">
 </package>

3. Install ProM package 'PNetReplayer'