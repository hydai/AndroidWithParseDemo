package xyz.hydai.demo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class MainActivity extends Activity {

	private TextView nicknameTextView;
	private EditText messageEditText;
	private Button sendButton;
	private ListView messagesListView;
	private ProgressBar loadingProgressBar;
	private String nickname;
	private List<ParseObject> messages;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get instance
		messageEditText = (EditText) findViewById(R.id.editText1);
		sendButton = (Button) findViewById(R.id.button2);
		messagesListView = (ListView) findViewById(R.id.listView1);
		loadingProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		nicknameTextView = (TextView) findViewById(R.id.textView1);
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});
		messageEditText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					sendMessage();
					return true;
				}
				return false;
			}
		});
		nickname = getIntent().getStringExtra("nickname");
		nicknameTextView.setText(nickname);
		loadDataFromParse();
		messagesListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> adapterView,
							View view, int position, long id) {
						messages.get(position).deleteInBackground(
								new DeleteCallback() {

									@Override
									public void done(ParseException e) {
										Toast.makeText(MainActivity.this,
												"deleteSuccess",
												Toast.LENGTH_SHORT).show();
										loadDataFromParse();
									}
								});
						return false;
					}
				});
	}

	private void sendMessage() {
		String textString = nickname + " : "
				+ messageEditText.getText().toString();
		Toast.makeText(MainActivity.this, textString, Toast.LENGTH_SHORT)
				.show();

		ParseObject testObject = new ParseObject("Message");
		testObject.put("text", textString);
		testObject.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				loadDataFromParse();
			}
		});

		ParsePush push = new ParsePush();
		push.setChannel("all");
		push.setMessage(textString);
		push.sendInBackground();
	}

	private void loadDataFromParse() {
		messagesListView.setVisibility(View.INVISIBLE);
		loadingProgressBar.setVisibility(View.VISIBLE);
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Message");
		query.orderByDescending("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				loadingProgressBar.setVisibility(View.GONE);
				messagesListView.setVisibility(View.VISIBLE);
				List<String> data = new ArrayList<String>();
				for (ParseObject object : objects) {
					String text = object.getString("text");
					data.add(text);
				}
				messages = objects;
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						MainActivity.this, android.R.layout.simple_list_item_1,
						data);
				messagesListView.setAdapter(adapter);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_refresh) {
			loadDataFromParse();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
