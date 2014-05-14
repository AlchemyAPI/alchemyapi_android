package com.alchemyapi.android.test;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alchemyapi.api.AlchemyAPI;
import com.alchemyapi.api.AlchemyAPI_NamedEntityParams;
import com.alchemyapi.android.test.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AlchemyAPI_Android_TestApp extends Activity {
	public EditText edittext;
	public TextView textview;
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
        
        edittext = (EditText) findViewById(R.id.entry);
        textview = (TextView) findViewById(R.id.TextView01);
        edittext.setText("http://techcrunch.com/2014/03/24/google-teams-with-ray-ban-and-oakley-maker-luxottica-for-future-versions-of-glass/");
        edittext.setSingleLine(true);
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
            	SendAlchemyCall("image");
            }
        });
        
        final Button combined_button = (Button) findViewById(R.id.combined_button);
        combined_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SendAlchemyCall("combined");
            }
        });

    }
    
    private void SendAlchemyCall(String call)
    {
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

    	String someString = edittext.getText().toString();
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
    	}
    	catch(Throwable t)
    	{
    		textview.setText("Error: " + t.getMessage());
    	}
    }
    
    private void ShowTagInTextView(Document doc, String tag)
    {
    	textview.setText("");
    	Element root = doc.getDocumentElement();
    	NodeList items = root.getElementsByTagName(tag);
    	for (int i=0;i<items.getLength();i++) {
        	Node concept = items.item(i);
        	String astring = concept.getNodeValue();
        	astring = concept.getChildNodes().item(0).getNodeValue(); 
        	textview.append("\n" + astring);
        }
    }
    
    private void ShowDocInTextView(Document doc, boolean showSentiment)
    {
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

}
