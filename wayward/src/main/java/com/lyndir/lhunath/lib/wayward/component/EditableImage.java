package com.lyndir.lhunath.lib.wayward.component;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.string.AppendingStringBuffer;


/**
 * <h2>{@link EditableImage}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 25, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class EditableImage extends Panel implements ModalWindow.WindowClosedCallback {

    private final IModel<FileUpload> file = new LoadableDetachableModel<FileUpload>() {
        @Override
        protected FileUpload load() {

            return null;
        }
    };
    private final ModalWindow window;
    private boolean editable;

    /**
     * @param id The wicket ID of the component.
     */
    protected EditableImage(final String id) {

        super( id );
        setOutputMarkupId( true );

        add( new Image( "image", new DynamicImageResource() {
            @Override
            protected byte[] getImageData() {

                return EditableImage.this.getImageData();
            }
        } ).add( new AttributeAppender( "class", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {

                return isEditable()? "link": "";
            }
        }, " " ) ).add( new AjaxEventBehavior( "onClick" ) {
            @Override
            protected void onEvent(final AjaxRequestTarget target) {

                if (isEditable())
                    window.show( target );
            }
        } ) );
        add( window = new ModalWindow( "upload" ) {

            {
                setInitialWidth( 300 );
                setInitialHeight( 100 );
                setContent( new UploadPanel( this, getContentId() ) );
                setWindowClosedCallback( EditableImage.this );
            }} );
    }

    /**
     * @param id                The wicket ID of the component.
     * @param initiallyEditable <code>true</code> if the image can be edited by clicking on it and uploading a new one.
     */
    protected EditableImage(final String id, final boolean initiallyEditable) {

        this( id );
        editable = initiallyEditable;
    }

    protected abstract byte[] getImageData();

    protected abstract void setImageData(byte[] imageData);

    /**
     * @param editable <code>true</code> if the image can be edited by clicking on it and uploading a new one.
     */
    public void setEditable(final boolean editable) {

        this.editable = editable;
    }

    /**
     * @return <code>true</code> if the image can be edited by clicking on it and uploading a new one.
     */
    public boolean isEditable() {

        return editable;
    }

    @Override
    public void onClose(final AjaxRequestTarget target) {

        if (isEditable() && file.getObject() != null)
            setImageData( file.getObject().getBytes() );

        target.addComponent( this );
    }

    private class UploadPanel extends Panel {

        private final Form<FileUpload> form;

        UploadPanel(final ModalWindow modalWindow, final String id) {

            super( id );

            add( (form = new Form<FileUpload>( "form", file ) {
                {
                    add( new FileUploadField( "file", file ) );
                }}).add( new AjaxFormSubmitBehavior( form, "onSubmit" ) {
                @Override
                protected void onSubmit(final AjaxRequestTarget target) {

                    modalWindow.close( target );
                }

                @Override
                protected void onError(final AjaxRequestTarget target) {

                }

                @Override
                protected CharSequence getEventHandler() {

                    // Prevents the form from generating an http request.
                    // If we do not provide this, the AJAX event is processed AND the form still gets submitted.
                    // FIXME: Ugly. Should probably be moved into AjaxFormSubmitBehaviour.
                    return new AppendingStringBuffer( super.getEventHandler() ).append( "; return false;" );
                }
            } ) );
        }
    }
}