package com.alchemyapi.android;


import java.io.ByteArrayOutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alchemyapi.android.R;
import com.alchemyapi.api.AlchemyAPI;
import com.alchemyapi.api.AlchemyAPI_ImageParams;
import com.alchemyapi.api.AlchemyAPI_NamedEntityParams;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	public TextView urlText;
	public TextView textview;
	public ImageView imageview;
	/****
	 * 
	 * Put your API Key into the variable below.  Can get key from http://www.alchemyapi.com/api/register.html
	 */
	public String AlchemyAPI_Key = "";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        urlText = (TextView) findViewById(R.id.entry);
        textview = (TextView) findViewById(R.id.TextView01);
        urlText.setText("http://techcrunch.com/2014/03/24/google-teams-with-ray-ban-and-oakley-maker-luxottica-for-future-versions-of-glass/");

        textview.setText("");
        textview.setMovementMethod(new ScrollingMovementMethod()); 
        
        
        final Button button = (Button) findViewById(R.id.concept_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SendAlchemyCall("concept");
            }
        });
        
        final Button entity_button = (Button) findViewById(R.id.entity_button);
        entity_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SendAlchemyCall("entity");
            }
        });
        
        final Button keyword_button = (Button) findViewById(R.id.keyword_button);
        keyword_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SendAlchemyCall("keyword");
            }
        });
        
        final Button text_button = (Button) findViewById(R.id.text_button);
        text_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SendAlchemyCall("text");
            }
        });
        
        final Button sentiment_button = (Button) findViewById(R.id.sentiment_button);
        sentiment_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SendAlchemyCall("sentiment");
            }
        });
        
        final Button taxonomy_button = (Button) findViewById(R.id.taxonomy_button);
        taxonomy_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SendAlchemyCall("taxonomy");
            }
        });
        
        final Button image_button = (Button) findViewById(R.id.image_button);
        image_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SendAlchemyCall("imageExtraction");
            }
        });
        
        final Button combined_button = (Button) findViewById(R.id.combined_button);
        combined_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SendAlchemyCall("combined");
            }
        });
        
        imageview = (ImageView) findViewById(R.id.imageView1);
        imageview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Log.d(getString(R.string.app_name), "CHANGE IMAGE");
            	Intent pickPhoto = new Intent(Intent.ACTION_PICK,
            			android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            	startActivityForResult(pickPhoto, 1);	
            }
        });
        
        final Button image_classify_button = (Button) findViewById(R.id.button_image_classify);
        image_classify_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SendAlchemyCall("imageClassify");
            }
        });

    }
    
    private void SendAlchemyCall(final String call)
    {
    	Thread thread = new Thread(new Runnable(){
    	    @Override
    	    public void run() {
    	        try {
    	        	SendAlchemyCallInBackground(call);
    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }
    	    }
    	});

    	thread.start(); 
    }
    
    private void SendAlchemyCallInBackground(final String call)
    {
    	runOnUiThread(new Runnable() { 
        	@Override
            public void run() {
		    	textview.setText("Making call: "+call);
        	}
    	});
    	
    	Document doc = null;
    	AlchemyAPI api = null;
    	try
    	{
    		api = AlchemyAPI.GetInstanceFromString(AlchemyAPI_Key);
    	}
    	catch( IllegalArgumentException ex )
    	{
    		textview.setText("Error loading AlchemyAPI.  Check that you have a valid AlchemyAPI key set in the AlchemyAPI_Key variable.  Keys available at alchemyapi.com.");
    		return;
    	}

    	String someString = urlText.getText().toString();
    	try{
    		if( "concept".equals(call) )
    		{
				doc = api.URLGetRankedConcepts(someString);
    			ShowDocInTextView(doc, false);
    		}
    		else if( "entity".equals(call))
    		{
    			doc = api.URLGetRankedNamedEntities(someString);
    			ShowDocInTextView(doc, false);
    		}
    		else if( "keyword".equals(call))
    		{
    			doc = api.URLGetRankedKeywords(someString);
    			ShowDocInTextView(doc, false);
    		}
    		else if( "text".equals(call))
    		{
    			doc = api.URLGetText(someString);
    			ShowDocInTextView(doc, false);
    		}
    		else if( "sentiment".equals(call))
    		{
    			AlchemyAPI_NamedEntityParams nep = new AlchemyAPI_NamedEntityParams();
    			nep.setSentiment(true);
    			doc = api.URLGetRankedNamedEntities(someString, nep);
    			ShowDocInTextView(doc, true);
    		}
    		else if( "taxonomy".equals(call))
    		{
    			doc = api.URLGetTaxonomy(someString);
    			ShowTagInTextView(doc, "label");
    		}
    		else if( "image".equals(call))
    		{
    			doc = api.URLGetImage(someString);
    			ShowTagInTextView(doc, "image");
    		}
    		else if( "combined".equals(call))
    		{
    			doc = api.URLGetCombined(someString);
    			ShowDocInTextView(doc, false);
    		}
    		else if( "imageClassify".equals(call))
    		{
    			Bitmap bitmap = ((BitmapDrawable)imageview.getDrawable()).getBitmap();
    			ByteArrayOutputStream stream = new ByteArrayOutputStream();
    			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
    			byte[] imageByteArray = stream.toByteArray();
    			
    	        AlchemyAPI_ImageParams imageParams = new AlchemyAPI_ImageParams();
    	        imageParams.setImage(imageByteArray);
    	        imageParams.setImagePostMode(AlchemyAPI_ImageParams.RAW);
    	        doc = api.ImageGetRankedImageKeywords(imageParams);
    			ShowTagInTextView(doc, "text");
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	 	textview.setText("Error: " + e.getMessage());
    	}
    }
    
    private void ShowTagInTextView(final Document doc, final String tag)
    {
    	Log.d(getString(R.string.app_name), doc.toString());
    	runOnUiThread(new Runnable() { 
        	@Override
            public void run() {
		    	textview.setText("Tags: \n");
		    	Element root = doc.getDocumentElement();
		    	NodeList items = root.getElementsByTagName(tag);
		    	for (int i=0;i<items.getLength();i++) {
		        	Node concept = items.item(i);
		        	String astring = concept.getNodeValue();
		        	astring = concept.getChildNodes().item(0).getNodeValue(); 
		        	textview.append("\n" + astring);
		        }
        	}
    	});
    }
    
    private void ShowDocInTextView(final Document doc, final boolean showSentiment)
    {
    	runOnUiThread(new Runnable() { 
        	@Override
            public void run() {
        		textview.setText("");
            	if( doc == null )
            	{
            		return;
            	}
            	
            	Element root = doc.getDocumentElement();
                NodeList items = root.getElementsByTagName("text");
                if( showSentiment )
                {
                	NodeList sentiments = root.getElementsByTagName("sentiment");
        	        for (int i=0;i<items.getLength();i++){
        	        	Node concept = items.item(i);
        	        	String astring = concept.getNodeValue();
        	        	astring = concept.getChildNodes().item(0).getNodeValue(); 
        	        	textview.append("\n" + astring);
        	        	if( i < sentiments.getLength() )
        	        	{
        	        		Node sentiment = sentiments.item(i);
        	        		Node aNode = sentiment.getChildNodes().item(1);
        	        		Node bNode = aNode.getChildNodes().item(0);
        	        		textview.append(" (" + bNode.getNodeValue()+")");
        	        	}
        	        }       	
                }
                else
                {
        	        for (int i=0;i<items.getLength();i++) {
        	        	Node concept = items.item(i);
        	        	String astring = concept.getNodeValue();
        	        	astring = concept.getChildNodes().item(0).getNodeValue(); 
        	        	textview.append("\n" + astring);
        	        }
                }        
        	}
       	});
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
    	super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 
    	if(resultCode == RESULT_OK){
			Uri selectedImage = imageReturnedIntent.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            
            try
            {
            	Bitmap imgBitmap = getScaledBitmap(picturePath, 400, 400);
            	
            	// Images from the filesystem might be rotated...
            	ExifInterface exif = new ExifInterface(picturePath);
            	int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            	
            	Log.d(getString(R.string.app_name), "Orientation: "+orientation);
            	
            	switch (orientation) {
            		case 3:
            			{
            				Matrix matrix = new Matrix();
            			
            				matrix.postRotate(90);
            				imgBitmap = Bitmap.createBitmap(
                    			imgBitmap, 0, 0, imgBitmap.getWidth(), imgBitmap.getHeight(), matrix, true);
            			}
            			break;
            		case 6:
	            		{
	        				Matrix matrix = new Matrix();
	        			
	        				matrix.postRotate(90);
	        				imgBitmap = Bitmap.createBitmap(
	                			imgBitmap, 0, 0, imgBitmap.getWidth(), imgBitmap.getHeight(), matrix, true);
	        			}
            			break;
            	
            	}
            	
            	imageview.setImageBitmap(imgBitmap);
            }
            catch (Exception e)
            {
            	textview.setText("Error loading image: " + e.getMessage());
            }
		}
    }
    
    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

}
