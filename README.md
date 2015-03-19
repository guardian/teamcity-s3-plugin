TeamCity S3 plugin
==================

Plugin that uploads all build artifacts to S3. Also uploads a `build.json` describing the build which created the
artifacts and a `tags.json` if the build has any tags added. Note that tagging a build isn't tied to a build, so this
may be created or updated after a build has finished.

Build
-----
Issue `mvn package` command from the root project to build the plugin. Resulting package s3-plugin.zip will be placed
in `target` directory.
 
Install
-------
To install the plugin, put zip archive to `plugins` dir under TeamCity data directory and restart the server.

 
