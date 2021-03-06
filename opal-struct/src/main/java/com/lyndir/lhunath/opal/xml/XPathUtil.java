package com.lyndir.lhunath.opal.xml;

import java.util.*;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Utilities for evaluating XPath on XHTML.
 *
 * @author mbillemo
 */
public class XPathUtil {

    private final XPath xpath;

    /**
     * Create a new XPathUtil instance.
     *
     * @param isXHTML {@code true}: Indicate that the document uses the XHTML namespace context and set it as the default context.
     */
    public XPathUtil(final boolean isXHTML) {

        xpath = XPathFactory.newInstance().newXPath();

        if (isXHTML)
            xpath.setNamespaceContext( new XHTMLContext() );
    }

    /**
     * Evaluate an {@link XPath} expression.
     *
     * @param context          The context to evaluate the XPath expression under.
     * @param expressionFormat The XPath expression format. See {@link String#format(String, Object...)}.
     * @param arguments        The data used to satisfy the format parameters in the expressionFormat.
     *
     * @return The text from the selected nodes
     *
     * @throws XPathExpressionException The given expression was not valid in the given context.
     */
    public Boolean getBoolean(final Object context, final String expressionFormat, final Object... arguments)
            throws XPathExpressionException {

        return (Boolean) getObject( context, expressionFormat, XPathConstants.BOOLEAN, arguments );
    }

    /**
     * Evaluate an {@link XPath} expression.
     *
     * @param context          The context to evaluate the XPath expression under.
     * @param expressionFormat The XPath expression format. See {@link String#format(String, Object...)}.
     * @param arguments        The data used to satisfy the format parameters in the expressionFormat.
     *
     * @return The first of the selected nodes.
     *
     * @throws XPathExpressionException The given expression was not valid in the given context.
     */
    public Node getNode(final Object context, final String expressionFormat, final Object... arguments)
            throws XPathExpressionException {

        return (Node) getObject( context, expressionFormat, XPathConstants.NODE, arguments );
    }

    /**
     * Evaluate an {@link XPath} expression.
     *
     * @param context          The context to evaluate the XPath expression under.
     * @param expressionFormat The XPath expression format. See {@link String#format(String, Object...)}.
     * @param arguments        The data used to satisfy the format parameters in the expressionFormat.
     *
     * @return A list of the selected nodes.
     *
     * @throws XPathExpressionException The given expression was not valid in the given context.
     */
    public List<Node> getNodes(final Object context, final String expressionFormat, final Object... arguments)
            throws XPathExpressionException {

        List<Node> nodeList = new ArrayList<>();
        NodeList annoyingNodeList = (NodeList) getObject( context, expressionFormat, XPathConstants.NODESET, arguments );

        for (int node = 0; node < annoyingNodeList.getLength(); ++node)
            nodeList.add( annoyingNodeList.item( node ) );

        return nodeList;
    }

    /**
     * Evaluate an {@link XPath} expression.
     *
     * @param context          The context to evaluate the XPath expression under.
     * @param expressionFormat The XPath expression format. See {@link String#format(String, Object...)}.
     * @param arguments        The data used to satisfy the format parameters in the expressionFormat.
     *
     * @return The text from the selected nodes.
     *
     * @throws XPathExpressionException The given expression was not valid in the given context.
     */
    public Number getNumber(final Object context, final String expressionFormat, final Object... arguments)
            throws XPathExpressionException {

        return (Number) getObject( context, expressionFormat, XPathConstants.NUMBER, arguments );
    }

    /**
     * Evaluate an {@link XPath} expression.
     *
     * @param context          The context to evaluate the XPath expression under.
     * @param expressionFormat The XPath expression format. See {@link String#format(String, Object...)}.
     * @param arguments        The data used to satisfy the format parameters in the expressionFormat.
     *
     * @return The text from the selected nodes.
     *
     * @throws XPathExpressionException The given expression was not valid in the given context.
     */
    public String getString(final Object context, final String expressionFormat, final Object... arguments)
            throws XPathExpressionException {

        return (String) getObject( context, expressionFormat, XPathConstants.STRING, arguments );
    }

    /**
     * Evaluate an {@link XPath} expression.
     *
     * @param context          The context to evaluate the XPath expression under.
     * @param expressionFormat The XPath expression format. See {@link String#format(String, Object...)}.
     * @param result           The type of result to return the selected nodes as.
     * @param arguments        The data used to satisfy the format parameters in the expressionFormat.
     *
     * @return The selected nodes as the given result type.
     *
     * @throws XPathExpressionException The given expression was not valid in the given context.
     */
    private Object getObject(final Object context, final String expressionFormat, final QName result, final Object... arguments)
            throws XPathExpressionException {

        String expression = String.format( expressionFormat, arguments );

        if (context instanceof InputSource)
            return xpath.evaluate( expression, (InputSource) context, result );

        return xpath.evaluate( expression, context, result );
    }

    /**
     * <h2>{@link XHTMLContext}<br> <sub>Namespace context for XHTML tags and attributes.</sub></h2>
     *
     * <p> <i>Apr 9, 2008</i> </p>
     *
     * @author mbillemo
     */
    private static class XHTMLContext implements NamespaceContext {

        private final Map<String, String> namespaces = new HashMap<>();

        /**
         * Create a new AuthDriver.XHTMLContext instance.
         */
        XHTMLContext() {

            namespaces.put( XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI );
            namespaces.put( XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI );
            namespaces.put( XMLConstants.DEFAULT_NS_PREFIX, "http://www.w3.org/1999/xhtml" );
            namespaces.put( "xhtml", "http://www.w3.org/1999/xhtml" );
        }

        @Override
        public String getNamespaceURI(final String prefix) {

            return namespaces.get( prefix );
        }

        @Nullable
        @Override
        public String getPrefix(final String namespaceURI) {

            for (final Map.Entry<String, String> namespace : namespaces.entrySet())
                if (namespaceURI.equals( namespace.getValue() ))
                    return namespace.getKey();

            return null;
        }

        @Override
        public Iterator<String> getPrefixes(final String namespaceURI) {

            Collection<String> uris = new ArrayList<>();
            for (final Map.Entry<String, String> namespace : namespaces.entrySet())
                if (namespaceURI.equals( namespace.getValue() ))
                    uris.add( namespace.getKey() );

            return uris.iterator();
        }
    }
}
