package edu.columbia.tg2401_ns2786;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends Activity implements View.OnClickListener{

	Button b_about, b_menu_translation, b_edge_detection;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		initialiation();
	}


	private void initialiation() {
		// TODO Auto-generated method stub
		b_about = (Button) findViewById (R.id.b_about);
		b_menu_translation = (Button) findViewById (R.id.b_menu_translation);
		b_edge_detection = (Button) findViewById (R.id.b_edge_detection);
		b_about.setOnClickListener(this);
		b_menu_translation.setOnClickListener(this);
		b_edge_detection.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.b_about:
			try {
				Class ourClass = Class.forName("edu.columbia.tg2401_ns2786.About");
				Intent ourIntent = new Intent(Menu.this, ourClass);
				startActivity(ourIntent);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case R.id.b_edge_detection:
			try {
				Class ourClass = Class.forName("edu.columbia.tg2401_ns2786.EdgeDetection");
				Intent ourIntent = new Intent(Menu.this, ourClass);
				startActivity(ourIntent);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case R.id.b_menu_translation:
			try {
				Class ourClass = Class.forName("edu.columbia.tg2401_ns2786.ChineseMenuRecognitionActivity");
				Intent ourIntent = new Intent(Menu.this, ourClass);
				startActivity(ourIntent);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;
		}
	}

}
