package com.lyndir.lhunath.lib.wayward.navigation;

import com.google.common.base.Splitter;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import com.lyndir.lhunath.lib.wayward.js.AjaxHooks;
import com.lyndir.lhunath.lib.wayward.js.JSUtils;
import java.net.URI;
import java.util.Map;
import org.apache.wicket.Component;
import org.apache.wicket.IClusterable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * <h2>{@link FragmentNavigationListener}<br> <sub>Listeners that implement support for fragment-based AJAX content navigation.</sub></h2>
 *
 * <p> To enable fragment-based navigation in your application, install the PageListener on the pages you want to enable fragment-based
 * navigation on, and add the AjaxRequestListener to each AjaxRequestTarget that can cause content state or navigation to change.</p>
 *
 * <p>The easiest way to do the latter is by overriding WebApplication#newAjaxRequestTarget(Page page) and adding the AjaxRequestListener to
 * the target there.</p>
 *
 * <p> <i>07 06, 2010</i> </p>
 *
 * @author lhunath
 */
public interface FragmentNavigationListener {

    abstract class Controller implements IClusterable {

        static final Logger logger = Logger.get( Controller.class );

        private String pageFragment;

        /**
         * Mark the given tab as active and restore state in its contents from the given tab-specific state arguments.
         *
         * @param tab      The tab that should be activated.
         * @param fragment The string that contains the fragment which needs to be parsed into tab-specific state.
         */
        <P extends Panel, S extends FragmentState<P, S>> void activateTabWithState(final FragmentNavigationTab<P, S> tab, final String fragment) {

            activateTabWithState( tab, tab.getState( fragment ) );
        }

        /**
         * Mark the given tab as active and restore state in its contents from the given tab-specific state arguments.
         *
         * @param tab   The tab that should be activated.
         * @param state The tab-specific state that should be applied to the tab's content.
         * @param <P>   The type of the tab's content.
         */
        public <P extends Panel, S extends FragmentState<P, S>> void activateTabWithState(final FragmentNavigationTab<P, S> tab, final S state) {

            P tabPanel = tab.getPanel( getTabContentId() );
            tab.applyFragmentState( tabPanel, state );

            activateTab( tab, tabPanel );
        }

        /**
         * Mark the given tab as active and create a new content panel for the tab.
         *
         * @param tab The tab that should be activated.
         */
        public void activateNewTab(final FragmentNavigationTab<?, ?> tab) {

            activateTab( tab, null );
        }

        /**
         * Mark the given tab as active and use the given tabPanel for its content.
         *
         * @param tab      The tab that should be activated.
         * @param tabPanel The panel that provides the tab's content or <code>null</code> if a new content panel should be created for the
         *                 tab.
         */
        public void activateTab(final FragmentNavigationTab<?, ?> tab, Panel tabPanel) {

            if (tabPanel == null)
                tabPanel = tab.getPanel( getTabContentId() );
            tabPanel.setOutputMarkupPlaceholderTag( true );

            setActiveTab( tab, tabPanel );
            updateNavigationComponents();
        }

        private void updateNavigationComponents() {

            AjaxRequestTarget target = AjaxRequestTarget.get();
            if (target != null)
                for (final Component component : getNavigationComponents())
                    target.addComponent( component );
        }

        /**
         * @return All components that should be updated whenever page navigation changes.
         */
        protected abstract Iterable<? extends Component> getNavigationComponents();

        /**
         * Invoked when a page is loaded to indicate the page's active tab as determined by fragment state.
         *
         * @param tab      The tab that needs to be activated.
         * @param tabPanel The panel that contains the tab's content as determined by fragment state.
         */
        protected abstract void setActiveTab(final FragmentNavigationTab<?, ?> tab, final Panel tabPanel);

        /**
         * @return The wicket ID that the tab's content panel should bind to when generated to apply fragment state on it.
         */
        protected abstract String getTabContentId();

        /**
         * Note:   The order should reflect the defaulting preference.  When no tab is selected by the fragment (or there is no fragment),
         * the first tab will be used instead, if visible.  If not visible, the next one will be tried, and so on.
         *
         * @return The application's tabs.
         */
        protected abstract Iterable<FragmentNavigationTab<?, ?>> getTabs();

        /**
         * @return The current fragment of the page.
         */
        public String getPageFragment() {

            return pageFragment;
        }

        /**
         * @param pageFragment The new fragment of the page.
         */
        public void setPageFragment(final String pageFragment) {

            this.pageFragment = pageFragment;
        }
    }


    class PageListener implements AjaxHooks.IPageListener {

        static final Logger logger = Logger.get( PageListener.class );

        private final Controller controller;

        /**
         * @param controller The object that controls fragment state for this page.
         */
        public PageListener(final Controller controller) {

            this.controller = controller;
        }

        @Override
        public void onReady(final AjaxRequestTarget target, final String pageUrl) {

            String fragment = URI.create( pageUrl ).getFragment();
            controller.setPageFragment( fragment );

            if (fragment != null) {
                // There is a fragment, load state from it.
                String tabFragment = Splitter.on( '/' ).split( fragment ).iterator().next();

                for (final FragmentNavigationTab<?, ? extends FragmentState<?, ?>> tab : controller.getTabs()) {
                    if (tab.getTabFragment().equalsIgnoreCase( tabFragment )) {
                        controller.activateTabWithState( tab, fragment );
                        return;
                    }
                }
            }

            // No fragment or fragment not recognised, find and set a default tab.
            for (final FragmentNavigationTab<?, ? extends FragmentState<?, ?>> tab : controller.getTabs()) {
                if (tab.isVisible()) {
                    controller.activateNewTab( tab );
                    return;
                }
            }

            throw logger.err( "Could not activate a tab for page; no tabs are visible." ).toError( IllegalStateException.class );
        }
    }


    abstract class AjaxRequestListener implements AjaxRequestTarget.IListener {

        static final Logger logger = Logger.get( AjaxRequestListener.class );

        private final Controller controller;

        /**
         * @param controller The object that controls fragment state for this page.
         */
        protected AjaxRequestListener(final Controller controller) {

            this.controller = controller;
        }

        @Override
        public void onBeforeRespond(final Map<String, Component> map, final AjaxRequestTarget target) {

        }

        @Override
        public void onAfterRespond(final Map<String, Component> map, final AjaxRequestTarget.IJavascriptResponse response) {

            updatePageFragment( getActiveTab(), response );
        }

        private <P extends Panel, S extends FragmentState<P, S>> void updatePageFragment(final FragmentNavigationTab<P, S> activeTab, final AjaxRequestTarget.IJavascriptResponse response) {

            Class<P> panelClass = activeTab.getPanelClass();
            Component contentPanel = getActiveContent();

            if (panelClass.isInstance( contentPanel )) {
                String newFragment = activeTab.getFragmentState( panelClass.cast( contentPanel ) ).toFragment();

                if (!ObjectUtils.equal( newFragment, controller.getPageFragment() )) {
                    response.addJavascript( "window.location.hash = " + JSUtils.toString( newFragment ) );
                    controller.updateNavigationComponents();
                }
            }
        }

        protected abstract Component getActiveContent();

        protected abstract FragmentNavigationTab<?, ? extends FragmentState<?, ?>> getActiveTab();
    }
}
