/**
 * Copyright (C) 2019 Daniele Baggio
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package au.com.permeance.utility.logviewer.portlets;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortletCategoryKeys;
import com.liferay.portal.kernel.util.StringPool;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;

/**
 * LogViewerPortlet
 *
 * @author Chun Ho <chun.ho@permeance.com.au>
 * @author @baxtheman
 */
@Component(
		immediate = true,
		property = {
			"com.liferay.portlet.portlet-name=Liferay Log",
			"javax.portlet.name=LogViewerPortlet",
			"javax.portlet.display-name=Liferay Log",
			"com.liferay.portlet.add-default-resource=true",
			"com.liferay.portlet.css-class-wrapper=portlet-controlpanel",
			"com.liferay.portlet.control-panel-entry-weight=0",
			"com.liferay.portlet.render-weight=100",
			"javax.portlet.expiration-cache=0",
			"javax.portlet.resource-bundle=content.Language",
			"javax.portlet.init-param.view-template=/view.jsp",
			"javax.portlet.security-role-ref=administrator",
			"javax.portlet.supports.mime-type=text/html"
		},
		service = Portlet.class
	)
public class LogViewerPortlet extends MVCPortlet {

	public static final String ATTRIB_CONTENT = "content";

	public static final String ATTRIB_ERROR = "error";

	public static final String ATTRIB_MODE = "mode";

	public static final String ATTRIB_POINTER = "pointer";

	public static final String ATTRIB_RESULT = "result";

	public static final String ATTRIB_TRACE = "trace";

	public static final String ERROR_PAGE =
		"/error.jsp";

	public static final String MODE_ATTACHED = "attached";

	public static final String MODE_DETACHED = "detached";

	public static final String OP_ATTACH = "attach";

	public static final String OP_DETACH = "detach";

	public static final String PARAM_OP = "cmd";

	public static final String RESULT_ERROR = "error";

	public static final String RESULT_SUCCESS = "success";

	public static final String VIEW_PAGE = "/view.jsp";

	public static final Log log = LogFactoryUtil.getLog(LogViewerPortlet.class);

	/**
	 * view method
	 */
	@Override
	public void doView(
			final RenderRequest renderRequest,
			final RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			include(VIEW_PAGE, renderRequest, renderResponse);
		} catch (final Exception e) {
			log.warn(e);
			include(ERROR_PAGE, renderRequest, renderResponse);
		}
	}

	/**
	 * serveResource method
	 */
	@Override
	public void serveResource(
		final ResourceRequest resourceRequest,
		final ResourceResponse resourceResponse) {

		try {
			resourceResponse.setContentType(PortletConstants.MIME_TYPE_JSON);
			resourceResponse.addProperty(
				HttpHeaders.CACHE_CONTROL, PortletConstants.NO_CACHE);

			final String cmd = resourceRequest.getParameter(PARAM_OP);

			if (OP_ATTACH.equals(cmd)) {
				try {
					LogHolder.attach();
					final JSONObject obj = JSONFactoryUtil.createJSONObject();
					obj.put(ATTRIB_RESULT, RESULT_SUCCESS);
					resourceResponse.getWriter().print(obj.toString());
				} catch (final Exception e) {
					final StringWriter sw = new StringWriter();
					final PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					pw.close();
					sw.close();

					final JSONObject obj = JSONFactoryUtil.createJSONObject();
					obj.put(ATTRIB_RESULT, RESULT_ERROR);
					obj.put(ATTRIB_ERROR, e.toString());
					obj.put(ATTRIB_TRACE, sw.toString());

					resourceResponse.getWriter().print(obj.toString());

					log.error(e);
				}
			} else if (OP_DETACH.equals(cmd)) {
				LogHolder.detach();
				final JSONObject obj = JSONFactoryUtil.createJSONObject();
				obj.put(ATTRIB_RESULT, RESULT_SUCCESS);
				resourceResponse.getWriter().print(obj.toString());
			} else {

				final int pointer = GetterUtil.getInteger(
					resourceRequest.getParameter(ATTRIB_POINTER), -1);

				final RollingLogViewer viewer = LogHolder.getViewer();

				int curpointer = -1;
				String content = StringPool.BLANK;
				String mode = MODE_DETACHED;

				if (viewer != null) {
					curpointer = viewer.getCurrentPointer();
					content = HtmlUtil.escape(
						new String(viewer.getBuffer(pointer, curpointer)));
					mode = MODE_ATTACHED;
				}

				final JSONObject obj = JSONFactoryUtil.createJSONObject();
				obj.put(ATTRIB_POINTER, String.valueOf(curpointer));
				obj.put(ATTRIB_CONTENT, content);
				obj.put(ATTRIB_MODE, mode);

				resourceResponse.getWriter().print(obj.toString());
			}
		} catch (Exception e) {
			log.warn(e);
		}
	}

}