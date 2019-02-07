<%--
Copyright (C) 2019 Daniele Baggio @baxtheman

This program is free software: you can redistribute it and/or modify it under the terms of the
GNU General Public License as published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If
not, see <http://www.gnu.org/licenses/>.
--%>

<%@ include file="init.jsp" %>

<%@ page import="au.com.permeance.utility.logviewer.portlets.LogViewerPortlet" %><%@
page import="au.com.permeance.utility.logviewer.portlets.PortletPropsValues" %>

<liferay-ui:success
	key="success"
	message="ui-request-processed-successfully"
/>

<liferay-ui:error
	key="error"
	message="ui-request-processed-error"
/>

<script type="text/javascript">
	window.errorThreshold = 10;
	window.consecutiveErrorCount = 0;
	window.resourcePointer = "-1";
	function poll() {
		var resourceMappingUrl = '<portlet:resourceURL/>';
		AUI().use('aui-io-request', function(A) {
			A.io.request(resourceMappingUrl, {
				method: 'POST', data: {
					"<portlet:namespace/><%= LogViewerPortlet.ATTRIB_POINTER %>": window.resourcePointer
				},
				dataType: 'json',
				on: {
					success: function() {
						try {
							if (typeof this.get('responseData') != 'undefined') {
								window.resourcePointer = this.get('responseData').pointer;
								document.getElementById("viewlog").innerHTML = document.getElementById("viewlog").innerHTML + this.get('responseData').content;
								document.getElementById("viewlogmode").innerHTML = this.get('responseData').mode;
								window.consecutiveErrorCount=0;
							} else {
								window.consecutiveErrorCount++;
								if (window.consecutiveErrorCount >= window.errorThreshold) {
									clearTimeout(window.pollingIntervalId);
									alert("Polling of the log has been stopped as the poll error limit has been reached. Please refresh the page to restart polling.");
									document.getElementById("viewlog").innerHTML = document.getElementById("viewlog").innerHTML + "\n\n\n------\nPolling of the log has been stopped as the poll error limit has been reached. Please refresh the page to restart polling.\n------";
								}
							}
						} catch(err) {
							window.consecutiveErrorCount++;
							if (window.consecutiveErrorCount >= window.errorThreshold) {
								clearTimeout(window.pollingIntervalId);
								alert("Polling of the log has been stopped as the poll error limit has been reached. Please refresh the page to restart polling.");
								document.getElementById("viewlog").innerHTML = document.getElementById("viewlog").innerHTML + "\n\n\n------\nPolling of the log has been stopped as the poll error limit has been reached. Please refresh the page to restart polling.\n------";
							}
						}
					},
					failure: function() {
						window.consecutiveErrorCount++;
						if (window.consecutiveErrorCount >= window.errorThreshold) {
							clearTimeout(window.pollingIntervalId);
							alert("Polling of the log has been stopped as the poll error limit has been reached. Please refresh the page to restart polling.");
							document.getElementById("viewlog").innerHTML = document.getElementById("viewlog").innerHTML + "\n\n\n------\nPolling of the log has been stopped as the poll error limit has been reached. Please refresh the page to restart polling.\n------";
						}
					}
				}
			});
		});
	}

	function detachlogger() {
		return sendCmd('<%= LogViewerPortlet.OP_DETACH %>');
	}

	function attachlogger() {
		return sendCmd('<%= LogViewerPortlet.OP_ATTACH %>');
	}

	function sendCmd(mycmd) {
		var resourceMappingUrl = '<portlet:resourceURL/>';
		AUI().use('aui-io-request', function(A) {
			A.io.request(resourceMappingUrl, {
				method: 'POST', data: {
					"<portlet:namespace/><%= LogViewerPortlet.PARAM_OP %>": mycmd
				},
				dataType: 'json',
				on: {
					success: function() {
						var result = this.get('responseData').result;
						if (result == '<%= LogViewerPortlet.RESULT_ERROR %>') {
							alert(this.get('responseData').error);
						}
					}
				}
			});
		});
	}

	window.pollingIntervalId = setInterval(poll, <%= String.valueOf(PortletPropsValues.PERMEANCE_LOG_VIEWER_REFRESH_INTERVAL) %>);
</script>

<div class="container">

	<div class="alert alert-info" role="alert">
		<span class="alert-indicator">
			<svg class="lexicon-icon lexicon-icon-info-circle" focusable="false" role="presentation">
				<use href="/images/icons/icons.svg#info-circle"></use>
			</svg>
		</span>

		<liferay-ui:message key="the-logger-is-currently" />
		<strong><span id="viewlogmode"><liferay-ui:message key="waiting-for-status" /></span>.</strong>
		<liferay-ui:message arguments="<%= new String[] {PortletPropsValues.PERMEANCE_LOG_VIEWER_REFRESH_INTERVAL_DISPLAY_SECONDS} %>" key="polling-every-x-seconds" />
	</div>


	<div class="navbar navbar-collapse-absolute navbar-expand-md ">
	<input class="btn btn-primary btn-sm" onClick="attachlogger(); return false;" type="button" value="<liferay-ui:message key="attach-logger" />" />
	<input class="btn btn-secondary btn-sm" onClick="detachlogger(); return false;" type="button" value="<liferay-ui:message key="detach-logger" />" />
	</div>

	<pre id="viewlog">
	</pre>

	<p>
	<em><liferay-ui:message key="you-can-set-portal-property" /> <b>permeance.log.viewer.autoattach</b> <liferay-ui:message key="autoattach-description" /></em><br />
	<em><liferay-ui:message key="you-can-set-portal-property" /> <b>permeance.log.viewer.pattern</b> <liferay-ui:message key="pattern-description" /></em><br /><br />
	</p>

</div>

<style>
#viewlog {
	margin: 10px 0px;
	min-height: 100px;
	background-color: #333;
	color: #ccc;
	font-size: 11px;
}
</style>