package baidumapsdk.demo.createmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import baidumapsdk.demo.R;
import baidumapsdk.demo.layers.LayerBuilding;
import baidumapsdk.demo.layers.LayerTrafficAndHeatMap;
import baidumapsdk.demo.util.DemoInfo;
import baidumapsdk.demo.util.DemoListAdapter;

public class CreateMapList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menulist);
        ListView demoList = (ListView) findViewById(R.id.mapList);
        // 添加ListItem，设置事件响应
        demoList.setAdapter(new DemoListAdapter(CreateMapList.this,DEMOS));
        demoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
                onListItemClick(index);
            }
        });
    }

    void onListItemClick(int index) {
        Intent intent;
        intent = new Intent(CreateMapList.this, DEMOS[index].demoClass);
        this.startActivity(intent);
    }

    private static final DemoInfo[] DEMOS = {
            new DemoInfo(R.string.demo_title_map_type, R.string.demo_desc_map_type, MapType.class),
            new DemoInfo(R.string.demo_title_custommap, R.string.demo_desc_custommap, CustomMap.class),
            new DemoInfo(R.string.demo_title_layertraffic, R.string.demo_desc_layertraffic, LayerTrafficAndHeatMap.class),
            new DemoInfo(R.string.demo_title_indoor, R.string.demo_desc_indoor, IndoorMap.class),
            new DemoInfo(R.string.demo_title_multimapview, R.string.demo_desc_multimapview, MultiMapView.class),
            new DemoInfo(R.string.demo_title_offline, R.string.demo_desc_offline, Offline.class)
    };
}

