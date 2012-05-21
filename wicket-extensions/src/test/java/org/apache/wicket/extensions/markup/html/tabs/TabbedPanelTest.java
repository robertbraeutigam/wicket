/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.extensions.markup.html.tabs;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.WicketTestCase;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.junit.Test;

public class TabbedPanelTest extends WicketTestCase
{
	public static class TestPage extends WebPage
	{
		public TabbedPanel<ITab> tabbedPanel;

		public TestPage()
		{
			List<ITab> defaultTabs = new ArrayList<ITab>();
			defaultTabs.add(new AbstractTab(Model.of("default 1"))
			{

				@Override
				public WebMarkupContainer getPanel(String panelId)
				{
					return new TestPanel(panelId, "default 1");
				}
			});
			defaultTabs.add(new AbstractTab(Model.of("default 2"))
			{

				@Override
				public WebMarkupContainer getPanel(String panelId)
				{
					return new TestPanel(panelId, "default 2");
				}
			});
			tabbedPanel = new TabbedPanel<ITab>("tabpanel", defaultTabs);
			add(tabbedPanel);
		}
	}

	public static class TestPanel extends Panel
	{
		public TestPanel(String id, String panelTestId)
		{
			super(id);
			this.add(new Label("label", panelTestId));
		}
	}

	@Test
	public void renderDefaultTabsOnly() throws Exception
	{
		tester.startPage(TestPage.class);
		tester.assertContains("<span wicket:id=\"title\">default 1</span></a>");
		tester.assertContains("<span wicket:id=\"label\">default 1</span>");
		tester.assertContains("<span wicket:id=\"title\">default 2</span></a>");
		tester.clickLink("tabpanel:tabs-container:tabs:1:link");
		tester.assertContains("<span wicket:id=\"label\">default 2</span>");
	}

	@Test
	public void renderAdditionalTabs() throws Exception
	{
		TestPage page = tester.startPage(TestPage.class);
		page.tabbedPanel.getTabs().add(new AbstractTab(Model.of("added 1"))
		{
			@Override
			public WebMarkupContainer getPanel(String panelId)
			{
				return new TestPanel(panelId, "added 1");
			}
		});
		// the additional tab isn't rendered yet
		tester.assertContainsNot("<span wicket:id=\"title\">added 1</span></a>");
		tester.assertContainsNot("<span wicket:id=\"label\">added 1</span>");

		// now its title is visible, but the contents not
		tester.clickLink("tabpanel:tabs-container:tabs:1:link");
		tester.assertContains("<span wicket:id=\"title\">added 1</span></a>");
		tester.assertContainsNot("<span wicket:id=\"label\">added 1</span>");

		// now the entire panel should be there
		tester.clickLink("tabpanel:tabs-container:tabs:2:link");
		tester.assertContains("<span wicket:id=\"title\">added 1</span></a>");
		tester.assertContains("<span wicket:id=\"label\">added 1</span>");
	}
}
