package com.myproject.huutam.test;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.myproject.huutam.test.adapter.CustomGridMission;
import com.myproject.huutam.test.dom.XMLDOMParser;
import com.myproject.huutam.test.item.MissionItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MissionActivity extends AppCompatActivity {

    private GridView gridMission;
    private ImageButton imgBack;
    ArrayList<MissionItem> missionItemArrayList;
    ArrayList<MissionItem> arrayListShow;
    CustomGridMission adapter;
    XMLDOMParser parser = new XMLDOMParser();
    String fileName = "level";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        gridMission = (GridView) findViewById(R.id.gridMission);
        imgBack = (ImageButton) findViewById(R.id.imgBack);
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            addList();
            for(int i = 0; i < 12; i++){
                String level = parser.writeUsingNormalOperation(missionItemArrayList.get(i));
                writeLevel(fileName + (i + 1) + ".xml", level);
            }
        }

        arrayListShow = new ArrayList<>();
        for(int i=0; i< 12; i++) {
            readLevels(fileName + (i + 1) + ".xml");
        }

        adapter = new CustomGridMission(getBaseContext(), R.layout.item_mission, arrayListShow);
        adapter.notifyDataSetChanged();
        gridMission.setAdapter(adapter);

        gridMission.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(arrayListShow.get(i).isLock()==true) {
                    Intent intentPlay = new Intent(MissionActivity.this, OpenImage.class);
                    intentPlay.putExtra("level", arrayListShow.get(i).getLevel());
                    finish();
                    startActivity(intentPlay);
                }
                else{
                    Toast.makeText(MissionActivity.this, "Coming soon...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMenu = new Intent(MissionActivity.this, MenuActivity.class);
                finish();
                startActivity(intentMenu);
            }
        });
    }

    private void readLevels(String fileLevel) {
        String currentLevel = readData(fileLevel);
        Document doc = parser.getDocument(currentLevel);
        NodeList node = doc.getElementsByTagName("level");
        Element element = (Element) node.item(0);
        int level = Integer.parseInt(element.getAttribute("id"));
        String lock = parser.getValue(element, "lock");
        Boolean lock_bool;
        if(lock.equals("true"))
            lock_bool = true;
        else lock_bool = false;
        int star = Integer.parseInt(parser.getValue(element, "star"));
        int background = Integer.parseInt(parser.getValue(element, "background"));

        MissionItem missionItem = new MissionItem(level, lock_bool, star, background);
//        Toast.makeText(this, background, Toast.LENGTH_SHORT).show();
        arrayListShow.add(missionItem);
    }

    private String readData(String fileLevel) {
        StringBuilder builder = new StringBuilder();
        String content = "";
        try {
            FileInputStream input = this.openFileInput(fileLevel);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
            while ((content = buffer.readLine()) != null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    builder.append(content).append('\n');
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error1:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error2:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return builder.toString();
    }

    public void writeLevel(String fileLevel, String level){

        try {
            FileOutputStream output = this.openFileOutput(fileLevel, MODE_PRIVATE);
            output.write(level.getBytes());
            output.close();
            //Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error1:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error2:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void addList() {

        missionItemArrayList = new ArrayList<>();
        for(int i = 0; i < 12; i++){
            if(i == 0){
                missionItemArrayList.add(new MissionItem(i + 1, true, R.drawable.nostar, R.drawable.mission));
            }
            else{
                missionItemArrayList.add(new MissionItem(i + 1, false, R.drawable.nostarmissionno, R.drawable.mission_no));
            }
        }
    }
}
