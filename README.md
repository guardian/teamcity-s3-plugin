TeamCity S3 plugin
==================

Plugin that uploads all build artifacts to S3. Also uploads a `build.json` describing the build which created the
artifacts and a `tags.json` if the build has any tags added. Note that tagging a build isn't tied to a build, so this
may be created or updated after a build has finished.

All of these will be uploaded to a directory of the form `projectname::buildname/buildnumber`.

You need to install a version matching your TeamCity version:

|   TeamCity    |  S3 Plugin |
|---------------|------------|
|   10.0.x      |   1.6.0    |
|   9.1.x       |   1.5.0    |
|   8.1x        |   1.4.0    |

Build
-----
Issue `mvn package` command from the root project to build the plugin. Resulting package s3-plugin.zip will be placed
in `target` directory.

The plugin is written in Scala, but interacts with Spring, JSP and various null happy Java APIs.
 
Install
-------
To install the plugin, put zip archive to `plugins` dir under TeamCity data directory and restart the server.

If all works correctly, your admin screen should look something like:

![Admin screen with S3 plugin](teamcity-admin-screenshot.png?raw=true) 
