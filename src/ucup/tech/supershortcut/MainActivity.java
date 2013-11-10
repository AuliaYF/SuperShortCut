package ucup.tech.supershortcut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

public class MainActivity extends Activity {
	LinearLayout applist;
	Button btn_add;
	ImageView img;

	final String TAG = "SuperShortCut";
	PackageManager pm;

	ArrayList<PInfo> res;

	class PInfo {
		private String appname = "";
		private String pname = "";
		private Drawable icon;
	}

	RelativeLayout.LayoutParams imgparam = new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		applist = (LinearLayout)findViewById(R.id.applist);

		btn_add = (Button)findViewById(R.id.btn_add);
		btn_add.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder dialog = createDialog();
				dialog.show();
			}
		});
	}

	private AlertDialog.Builder createDialog(){
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(
				MainActivity.this);
		builderSingle.setTitle("Pick an App: ");
		res = new ArrayList<PInfo>();
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				MainActivity.this, android.R.layout.select_dialog_singlechoice);
		for (ResolveInfo rInfo : getApps()) {

			PInfo newInfo = new PInfo();
			arrayAdapter.add(rInfo.activityInfo.applicationInfo.loadLabel(pm).toString());

			newInfo.appname = rInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
			newInfo.pname = rInfo.activityInfo.packageName.toString();
			newInfo.icon = rInfo.activityInfo.loadIcon(pm);

			res.add(newInfo);
		}
		Collections.sort(res, new Comparator<PInfo>() {
			@Override
			public int compare(PInfo lhs, PInfo rhs) {
				return lhs.appname.compareTo(rhs.appname);
			}
		});
		arrayAdapter.sort(new Comparator<String>() {
			public int compare(String object1, String object2) {
				return object1.compareTo(object2);
			};
		});
		builderSingle.setAdapter(arrayAdapter,
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, final int which) {
				img = new ImageView(MainActivity.this);
				img.setLayoutParams(imgparam);
				img.setTag(res.get(which).appname);
				img.setImageDrawable(res.get(which).icon);
				if(getIcon(applist, res.get(which).appname)){
					return;
				}
				img.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(res.get(which).pname);
						startActivity(LaunchIntent);
					}
				});
				
				applist.addView(img);
			}
		});
		return builderSingle;
	}

	private ArrayList<ResolveInfo> getApps(){

		pm = getPackageManager();

		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		ArrayList<ResolveInfo> list = (ArrayList<ResolveInfo>) pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);
		return list;
	}
	private void removeIcon(LinearLayout view, String tag){
		int childCount = view.getChildCount();
		View child;
		for (int i = 0; i <= childCount - 1; i++) {
			child = view.getChildAt(i);
			if(child.getTag() == tag){
				view.removeView(child);
			}
		}
	}

	private boolean getIcon(LinearLayout view, String tag){
		int childCount = view.getChildCount();
		View child;
		for (int i = 0; i <= childCount - 1; i++) {
			child = view.getChildAt(i);
			if(child.getTag() == tag){
				return true;
			}
		}
		return false;
	}
}
