package com.dream.dreamlauncher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private GridView mGrid;
    private List<ResolveInfo> mApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadApps();
        mGrid = (GridView)findViewById(R.id.apps_list);
        mGrid.setAdapter(new AppsAdapter(this, mApps));
        mGrid.setOnItemClickListener(listener);
    }

    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
    }

    private class AppsAdapter extends BaseAdapter {
        private Context mContext;
        private PackageManager pm;
        private List<ResolveInfo> mList;

        public AppsAdapter(Context context, List<ResolveInfo> list) {
            mContext = context;
            pm = mContext.getPackageManager();
            mList = new ArrayList<ResolveInfo>();
            for (int i = 0; i < list.size(); i++) {
                mList.add(list.get(i));
            }
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mApps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ResolveInfo appInfo = mApps.get(position);
            AppItem appItem;
            if (convertView == null) {
                View v = LayoutInflater.from(mContext).inflate(R.layout.appicon, null);

                appItem = new AppItem();
                appItem.mAppIcon = (ImageView)v.findViewById(R.id.appicon);
                appItem.mAppTitle = (TextView)v.findViewById(R.id.apptitle);

                v.setTag(appItem);
                convertView = v;
            }
            else {
                appItem = (AppItem)convertView.getTag();
            }

            appItem.mAppIcon.setBackground(appInfo.loadIcon(pm));
            appItem.mAppTitle.setText(appInfo.loadLabel(pm));

            return convertView;
        }
    }

    //Listener to all app
    private OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i("MyLauncher", "Listening.");
            ResolveInfo info = mApps.get(position);
            //Package name
            String pkg = info.activityInfo.packageName;
            Log.i("MyLauncher", "Package Name: " + pkg);
            //Activity name
            String cls = info.activityInfo.name;
            Log.i("MyLauncher", "Activity Name: " + cls);
            ComponentName componet = new ComponentName(pkg,cls);
            Intent i = new Intent();
            i.setComponent(componet);
            startActivity(i);
        }
    };

    private class AppItem {
        ImageView mAppIcon;
        TextView mAppTitle;
    }
}
