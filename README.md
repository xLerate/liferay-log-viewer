
# Liferay Log Viewer Portlet

## FORK & REVAMP FROM THE ORIGINAL GOOD LOG VIEWER BY Permeance (Thank you!!)

This project provides a portlet that attaches to the Liferay Portal's log4j mechanism to provide a log view within the browser.

## Supported Products

* Liferay Portal 7.1 CE
* Liferay Portal 7.1 DXP
* Liferay Portal 7.2 CE

## Downloads

Download bundle jar for 7.1.x CE: [au.com.permeance.utility.logviewer-7.1.2.1.jar](https://github.com/baxtheman/liferay-log-viewer/releases/download/7.1.2.1/au.com.permeance.utility.logviewer-7.1.2.1.jar)

Download bundle jar for 7.1.x DXP: [au.com.permeance.utility.logviewer.jar](https://github.com/baxtheman/liferay-log-viewer/releases/download/7.1.2.2/au.com.permeance.utility.logviewer.jar)

Download bundle jar for 7.2.0 CE: [au.com.permeance.utility.logviewer-7.2.0.1.jar](https://github.com/baxtheman/liferay-log-viewer/releases/download/7.2.0.1/au.com.permeance.utility.logviewer-7.2.0.1.jar)

## Usage

Administrators will see a "Log Viewer" portlet in the Configuration area of the Control Panel in 7.1 / 7.2.

![Screenshot](/doc/screenshot.jpg)

The portlet polls the log buffer every 2 seconds to update the page with the latest logs.
You can also attach or detach the logger from the portal log4j. (when detached, the portlet does not cause any overhead on portal operations).

Two portal properties can be set:
* *permeance.log.viewer.autoattach* to toggle whether the logger attaches automatically on startup, default "true".
* *permeance.log.viewer.pattern* to configure the log4j pattern, default "%d{ABSOLUTE} %-5p \[%c{1}:%L\] %m%n"

## Plugin Security

This plugin comes with the PACL Security Manager disabled.
However the list of PACL requirements to run this plugin in secure mode is available (commented out) in WEB-INF/liferay-plugin-package.properties. You can uncomment those entries to run the plugin in PACL secure mode.

## License

Liferay Log Viewer is available under GNU Public License version 3.0 (GPLv3). A copy of the license is attached in the code package.

## Project Team

* Current **me**
* Original Chun Ho - chun.ho@permeance.com.au
