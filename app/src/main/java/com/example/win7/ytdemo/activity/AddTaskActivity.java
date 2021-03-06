package com.example.win7.ytdemo.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.example.win7.ytdemo.R;
import com.example.win7.ytdemo.YApplication;
import com.example.win7.ytdemo.adapter.ZiAdapter;
import com.example.win7.ytdemo.entity.TaskEntry;
import com.example.win7.ytdemo.entity.Tasks;
import com.example.win7.ytdemo.util.Consts;
import com.example.win7.ytdemo.task.SubmitTask;
import com.example.win7.ytdemo.util.PinyinComparator;
import com.example.win7.ytdemo.view.CustomDatePicker;
import com.example.win7.ytdemo.view.CustomProgress;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView tv_huilv,tv_zuzhi,tv_quyu,tv_content,tv_respon,tv_zhidan,tv_contacts,tv_bibie,tv_jl;
    ListView lv_zb;
    List<HashMap<String,String>> list,list1,ziList;
    List<String> strList,strList1;
    DecimalFormat df = new DecimalFormat("#0.00");
    DecimalFormat df1 = new DecimalFormat("#0.0000");
    String interid,taskno,respon,zhidan,contacts,content,contentid
            ,planid,sup,jiliang,jiliangid,pfid,zuzhi,quyu,zhidu1,zhidu2,username,depart,company;
    int currencyid=1;
    String currency = "人民币";
    Double huilv = 1.00;
    int year,month,day;
    ZiAdapter adapter;
    Button btn_submit;
    Tasks tasks;
    CustomProgress progress;
    private String TAG = "AddTaskActivity";
    Double taxrate,seccoefficient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        setTool();
        setViews();
        setListeners();
    }

    protected void setTool(){
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        toolbar.setTitle(R.string.addtask);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AddTaskActivity.this)
                        .setTitle("确认退出？").setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_add:
                        HashMap<String,String> map = new HashMap<>();
                        try {
                            showDialog(map);
                        }catch (ParseException e){
                            e.printStackTrace();
                        }
                        break;
                }
                return true;
            }
        });
    }

    protected void showDialog(final HashMap<String,String> map) throws ParseException {
        if(tv_content.getText().toString().equals("")){
            Toast.makeText(AddTaskActivity.this,"请先选择内容",Toast.LENGTH_SHORT).show();
        }else {
            if(!map.isEmpty()){
//                jiliangid = map.get("jiliangid");
                planid = map.get("planid");
                pfid = map.get("pfid");
            }
            final View v = getLayoutInflater().inflate(R.layout.item_zi_add,null);
            final TextView tv_fuzhu = (TextView)v.findViewById(R.id.tv_fuzhu_add);
            tv_fuzhu.setText(sup);
            final EditText et_shuliang = (EditText)v.findViewById(R.id.et_shuliang);
            final EditText et_danjia = (EditText)v.findViewById(R.id.et_danjia);
            final EditText et_note = (EditText)v.findViewById(R.id.et_note);
            final EditText et_hanshui = (EditText)v.findViewById(R.id.et_hanshui);
            final EditText et_buhan = (EditText)v.findViewById(R.id.et_buhan);
            et_buhan.setEnabled(false);
            final EditText et_fuliang = (EditText)v.findViewById(R.id.et_fuliang);
            et_fuliang.setEnabled(false);
            final EditText et_fasong = (EditText)v.findViewById(R.id.et_fasong);
            final EditText et_huikui = (EditText)v.findViewById(R.id.et_huikui);
            final TextWatcher shuliang = new TextWatcher(){

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(!TextUtils.isEmpty(editable.toString())){
                        if (seccoefficient!=0.0000000000){
                        Double fuliang = Double.parseDouble(editable.toString())/seccoefficient;
                        et_shuliang.removeTextChangedListener(this);
                        et_fuliang.setText(df1.format(fuliang));
                        et_shuliang.addTextChangedListener(this);
                        }
                        if (!TextUtils.isEmpty(et_danjia.getText().toString())) {
                            Double hanshui = Double.parseDouble(editable.toString()) * Double.parseDouble(et_danjia.getText().toString());
                            Double buhan = hanshui/(taxrate+1)*huilv;
                            et_shuliang.removeTextChangedListener(this);
                            et_hanshui.setText(df.format(hanshui));
                            et_buhan.setText(df.format(buhan));
                            et_shuliang.addTextChangedListener(this);
                        }
                    }else{
                        et_shuliang.removeTextChangedListener(this);
                        et_hanshui.setText("");
                        et_fuliang.setText("");
                        et_buhan.setText("");
                        et_shuliang.addTextChangedListener(this);
                    }

                }
            };
            final TextWatcher hanshui = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(!TextUtils.isEmpty(et_danjia.getText().toString())&&!TextUtils.isEmpty(editable.toString())){
                        Double shuliang = Double.parseDouble(et_hanshui.getText().toString())/Double.parseDouble(et_danjia.getText().toString());
                        Double fuliang = shuliang/seccoefficient;
                        Double buhan = Double.parseDouble(editable.toString())/(taxrate+1)*huilv;
                        et_hanshui.removeTextChangedListener(this);
                        et_shuliang.setText(df1.format(shuliang));
                        if(seccoefficient!=0.0000000000) {
                            et_fuliang.setText(df1.format(fuliang));
                        }
                        et_buhan.setText(df.format(buhan));
                        et_hanshui.addTextChangedListener(this);
                    }else{
                        et_hanshui.removeTextChangedListener(this);
                        et_shuliang.setText("");
                        et_fuliang.setText("");
                        et_buhan.setText("");
                        et_hanshui.addTextChangedListener(this);
                    }
                }
            };
            et_shuliang.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b){
                        Log.i(TAG,"数量框获得了焦点");
                        et_shuliang.addTextChangedListener(shuliang);
                        et_hanshui.removeTextChangedListener(hanshui);
                    }
                }
            });

            et_danjia.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                        if(!TextUtils.isEmpty(et_shuliang.getText().toString())&&!TextUtils.isEmpty(editable.toString())){
                            Double hanshui = Double.parseDouble(et_shuliang.getText().toString())*Double.parseDouble(editable.toString());
                            Double buhan = hanshui/(taxrate+1)*huilv;
                            et_danjia.removeTextChangedListener(this);
                            et_hanshui.setText(df.format(hanshui));
                            et_buhan.setText(df.format(buhan));
                            et_danjia.addTextChangedListener(this);
                        }else{
                            et_shuliang.removeTextChangedListener(this);
                            et_hanshui.setText("");
                            et_buhan.setText("");
                            et_shuliang.addTextChangedListener(this);
                        }
                }
            });
            et_hanshui.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b) {
                        Log.i(TAG, "含税金额框获得了焦点");
                        et_hanshui.addTextChangedListener(hanshui);
                        et_shuliang.removeTextChangedListener(shuliang);
                    }
                }
            });

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            final String now = sdf.format(new Date());
            final TextView tv_qi = (TextView)v.findViewById(R.id.tv_qi_add);
            tv_qi.setText(now);
            tv_qi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    DatePickerDialog dpd = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                            String s;
//                            if(monthOfYear+1<10&&dayOfMonth<10){
//                                s = year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth;
//                            }else if(monthOfYear+1<10){
//                                s = year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth;
//                            }else if(dayOfMonth<10){
//                                s = year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth;
//                            }else{
//                                s = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
//                            }
//                            tv_qi.setText(s);
//                        }
//                    }, year, month, day);
//                    dpd.show();//显示DatePickerDialog组件
                    CustomDatePicker dpk = new CustomDatePicker(v.getContext(), new CustomDatePicker.ResultHandler() {
                        @Override
                        public void handle(String time) { // 回调接口，获得选中的时间
                            tv_qi.setText(time);
                        }
                    }, "2010-01-01 00:00", "2090-12-31 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
                    dpk.showSpecificTime(true); // 显示时和分
                    dpk.setIsLoop(true); // 允许循环滚动
                    dpk.show(tv_qi.getText().toString());
                }
            });
            final TextView tv_zhi = (TextView)v.findViewById(R.id.tv_zhi_add);
            tv_zhi.setText(now);
            tv_zhi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    DatePickerDialog dpd = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                            String s;
//                            if(monthOfYear+1<10&&dayOfMonth<10){
//                                s = year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth;
//                            }else if(monthOfYear+1<10){
//                                s = year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth;
//                            }else if(dayOfMonth<10){
//                                s = year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth;
//                            }else{
//                                s = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
//                            }
//                            tv_zhi.setText(s);
//                        }
//                    }, year, month, day);
//                    dpd.show();//显示DatePickerDialog组件
                    CustomDatePicker dpk = new CustomDatePicker(v.getContext(), new CustomDatePicker.ResultHandler() {
                        @Override
                        public void handle(String time) { // 回调接口，获得选中的时间
                            tv_zhi.setText(time);
                        }
                    }, "2010-01-01 00:00", "2090-12-31 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
                    dpk.showSpecificTime(true); // 显示时和分
                    dpk.setIsLoop(true); // 允许循环滚动
                    dpk.show(tv_zhi.getText().toString());
                }
            });
            final TextView tv_progress = (TextView)v.findViewById(R.id.tv_progress_add);
            final TextView tv_plan = (TextView)v.findViewById(R.id.tv_plan_add);
            final TextView tv_budget = (TextView)v.findViewById(R.id.tv_budget_add);
            final TextView tv_pbudget = (TextView)v.findViewById(R.id.tv_pbudget_add);
            tv_progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText et = new EditText(v.getContext());
                    new AlertDialog.Builder(v.getContext()).setTitle("计划预算进度").setView(et)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new JHTask(tv_progress,tv_plan,tv_pbudget,tv_budget,et.getText().toString()).execute();
                                }
                            }).setNegativeButton("取消",null).show();
                }
            });
            final TextView tv_pingfen = (TextView)v.findViewById(R.id.tv_pingfen_add);
            tv_pingfen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new PFTask(tv_pingfen).execute();
                }
            });
            final TextView tv_submit = (TextView)v.findViewById(R.id.tv_submit);
            if(!map.isEmpty()){
                et_shuliang.setText(map.get("shuliang"));
                et_danjia.setText(map.get("danjia"));
                tv_qi.setText(sdf.format(DateFormat.getInstance().parse(map.get("qi"))));
                tv_zhi.setText(sdf.format(DateFormat.getInstance().parse(map.get("zhi"))));
                tv_progress.setText(map.get("progress"));
                tv_plan.setText(map.get("plan"));
                tv_budget.setText(map.get("budget"));
                tv_pbudget.setText(map.get("pbudget"));
                et_note.setText(map.get("note"));
                et_hanshui.setText(map.get("hanshui"));
                et_buhan.setText(map.get("buhan"));
                et_fuliang.setText(map.get("fuliang"));
                et_fasong.setText(map.get("fasong"));
                et_huikui.setText(map.get("huikui"));
                tv_pingfen.setText(map.get("pingfen"));
            }
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(v)
                    .show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            tv_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!map.isEmpty()){
                        ziList.remove(map);
                    }
                    if(tv_qi.getText().toString().equals("")){
                        Toast.makeText(AddTaskActivity.this,"请选择启日期",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(tv_zhi.getText().toString().equals("")){
                        Toast.makeText(AddTaskActivity.this,"请选择止日期",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(tv_progress.getText().toString().equals("")){
                        Toast.makeText(AddTaskActivity.this,"请选择计划预算进度",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    map.put("neirong",content);
                    Log.i("计量",jiliang);
                    map.put("jiliang",jiliang);
                    map.put("jiliangid",jiliangid);
                    if(et_shuliang.getText().toString().equals("")){
                        map.put("shuliang","0");
                    }else {
                        map.put("shuliang", et_shuliang.getText().toString());
                    }
                    if(et_danjia.getText().toString().equals("")){
                        map.put("danjia","0");
                    }else {
                        map.put("danjia", et_danjia.getText().toString());
                    }
                    map.put("qi",tv_qi.getText().toString());
                    map.put("zhi",tv_zhi.getText().toString());
                    map.put("progress",tv_progress.getText().toString());
                    map.put("planid",planid);
                    map.put("plan",tv_plan.getText().toString());
                    map.put("budget",tv_budget.getText().toString());
                    map.put("pbudget",tv_pbudget.getText().toString());
                    map.put("note",et_note.getText().toString());
                    if(et_hanshui.getText().toString().equals("")){
                        map.put("hanshui","0");
                    }else {
                        map.put("hanshui", et_hanshui.getText().toString());
                    }
                    if(et_buhan.getText().toString().equals("")){
                        map.put("buhan","0");
                    }else {
                        map.put("buhan", et_buhan.getText().toString());
                    }
                    map.put("fuzhu",tv_fuzhu.getText().toString());
                    if(et_fuliang.getText().toString().equals("")){
                        map.put("fuliang","0");
                    }else{
                        map.put("fuliang", et_fuliang.getText().toString());
                    }
                    map.put("fasong",et_fasong.getText().toString());
                    map.put("huikui",et_huikui.getText().toString());
                    map.put("pingfen",tv_pingfen.getText().toString());
                    if(pfid==null){
                        map.put("pfid","0");
                    }else {
                        map.put("pfid", pfid);
                    }
                    ziList.add(map);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2,menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(AddTaskActivity.this)
                    .setTitle("确认退出？").setNegativeButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void setViews(){
        Calendar mycalendar = Calendar.getInstance();
        year = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
        month = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
        day = mycalendar.get(Calendar.DAY_OF_MONTH);
        list = new ArrayList<>();
        list1 = new ArrayList<>();
        strList = new ArrayList<>();
        strList1 = new ArrayList<>();
        tasks = new Tasks();
        tv_bibie = (TextView) findViewById(R.id.tv_bibie);
        tv_huilv = (TextView)findViewById(R.id.tv_huilv);
        tv_zuzhi = (TextView)findViewById(R.id.tv_zuzhi);
        tv_quyu = (TextView)findViewById(R.id.tv_quyu);
        tv_content = (TextView)findViewById(R.id.tv_content_add);
        tv_jl = (TextView)findViewById(R.id.tv_jl);
        tv_respon = (TextView)findViewById(R.id.tv_respon_add);
        tv_zhidan = (TextView)findViewById(R.id.tv_zhidan_add);
        tv_contacts = (TextView)findViewById(R.id.tv_contacts_add);
        lv_zb = (ListView)findViewById(R.id.lv_zb);
        btn_submit = (Button)findViewById(R.id.btn_submit_add);
        interid = getIntent().getStringExtra("interid");
        taskno = getIntent().getStringExtra("taskno");
        ziList = new ArrayList<>();
        if(interid.equals("0")) {
            adapter = new ZiAdapter(AddTaskActivity.this, ziList);
            lv_zb.setAdapter(adapter);
            new MRTask().execute();
        }else{
            tasks.setFbillno(taskno);
            tasks.setFinterid(interid);
            toolbar.setTitle("编辑任务");
            new DeTask(taskno).execute();
            new DeEntryTask(taskno).execute();
        }
    }

    protected void setListeners(){
        tv_zuzhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DepartsTask().execute();
            }
        });
        tv_quyu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AreaTask().execute();
            }
        });
        tv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText et = new EditText(AddTaskActivity.this);
                new AlertDialog.Builder(AddTaskActivity.this).setTitle("内容").setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new ItemTask(et.getText().toString()).execute();
                            }
                        }).setNegativeButton("取消",null).show();

            }
        });
        tv_jl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new JLTask().execute();
            }
        });
        tv_respon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText et = new EditText(AddTaskActivity.this);
                new AlertDialog.Builder(AddTaskActivity.this).setTitle("职员").setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new EmpTask(0,et.getText().toString()).execute();
                            }
                        }).setNegativeButton("取消",null).show();

            }
        });
//        tv_zhidan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new EmpTask(1).execute();
//            }
//        });
        tv_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText et = new EditText(AddTaskActivity.this);
                new AlertDialog.Builder(AddTaskActivity.this).setTitle("职员").setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new EmpTask(2,et.getText().toString()).execute();
                            }
                        }).setNegativeButton("取消",null).show();
            }
        });
        lv_zb.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int index, long l) {
                new AlertDialog.Builder(AddTaskActivity.this).setItems(new String[]{"编辑", "删除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                Toast.makeText(AddTaskActivity.this,"正在开发中...",Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                ziList.remove(index);
                                adapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }).show();
                return true;
            }
        });
        lv_zb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String,String> map = ziList.get(i);
                jiliang = map.get("jiliang");
                try {
                    showDialog(map);
                }catch (ParseException e){
                    e.printStackTrace();
                }
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(zuzhi==null){
                    Toast.makeText(AddTaskActivity.this,"请选择组织机构",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(quyu==null){
                    Toast.makeText(AddTaskActivity.this,"请选择区域部门",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(contentid==null){
                    Toast.makeText(AddTaskActivity.this,"请选择内容",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(respon==null){
                    Toast.makeText(AddTaskActivity.this,"请选择责任人",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(zhidan==null){
                    Toast.makeText(AddTaskActivity.this,"请选择制单人",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(contacts==null){
                    Toast.makeText(AddTaskActivity.this,"请选择往来",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(ziList.size()==0){
                    Toast.makeText(AddTaskActivity.this,"请添加子表信息",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(interid.equals("0")) {
                    tasks.setFinterid("0");
                    tasks.setFbillno("a");
                }
                tasks.setFBase3(String.valueOf(currencyid));
                tasks.setFAmount4(Double.parseDouble(tv_huilv.getText().toString()));
                tasks.setFBase11(zuzhi);
                tasks.setFBase12(quyu);

                List<TaskEntry> list = new ArrayList<>();
                for(int i=0;i<ziList.size();i++){
                    TaskEntry entry = new TaskEntry();
                    if(interid.equals("0")) {
                        entry.setFTime(ziList.get(i).get("qi") + ":00");
                        entry.setFTime1(ziList.get(i).get("zhi") + ":00");
                    }else{
                        entry.setFTime(ziList.get(i).get("qi"));
                        entry.setFTime1(ziList.get(i).get("zhi"));
                    }
                    entry.setFBase4(respon);
                    entry.setFBase15(zhidan);
                    entry.setFBase10(contacts);
                    entry.setFBase1(contentid);
                    entry.setFBase(ziList.get(i).get("planid"));
                    entry.setFNOTE(ziList.get(i).get("note"));
                    entry.setFBase2(ziList.get(i).get("jiliangid"));
                    entry.setFDecimal(Double.parseDouble(ziList.get(i).get("shuliang")));
                    entry.setFDecimal1(Double.parseDouble(ziList.get(i).get("danjia")));
                    entry.setFDecimal2(Double.parseDouble(ziList.get(i).get("fuliang")));
                    entry.setFAmount2(Double.parseDouble(ziList.get(i).get("hanshui")));
                    entry.setFAmount3(Double.parseDouble(ziList.get(i).get("buhan")));
                    entry.setFText(ziList.get(i).get("fasong"));
                    entry.setFText1(ziList.get(i).get("huikui"));
                    entry.setFBase14(ziList.get(i).get("pfid"));
                    entry.setFBase5("0");
                    entry.setFBase6("0");
                    entry.setFBase7("0");
                    entry.setFBase8("0");
                    entry.setFBase9("0");
                    list.add(entry);
                }
                tasks.setEntryList(list);
                new SubmitTask(tasks,AddTaskActivity.this).execute();
            }
        });
    }
    //默认字段填充
    class MRTask extends AsyncTask<Void,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "JA_select";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/JA_select";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            rpc.addProperty("FSql", "select a.fname username,a.fitemid responid,b.fname depart,b.fitemid departid,c.FName company,c.fitemid companyid from t_User d inner join  t_Emp a on d.FEmpID=a.fitemid left join t_Department b on a.FDepartmentID=b.FItemID left join t_Item_3001 c on c.FItemID=b.f_102 where d.FName='"+ YApplication.fname+"'");
            rpc.addProperty("FTable", "t_user");

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = true;
            // 等价于envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE transport = new HttpTransportSE(endPoint);
            try {
                // 调用WebService
                transport.call(soapAction, envelope);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("MeFragment", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            if (null != object) {
                Log.i("返回结果", object.getProperty(0).toString()+"=========================");
                String result = object.getProperty(0).toString();
                Document doc = null;

                try {
                    doc = DocumentHelper.parseText(result); // 将字符串转为XML

                    Element rootElt = doc.getRootElement(); // 获取根节点

                    System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称


                    Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head

                    // 遍历head节点
                    while (iter.hasNext()) {
                        Element recordEle = (Element) iter.next();
                        username = recordEle.elementTextTrim("username"); // 拿到head节点下的子节点title值
                        depart = recordEle.elementTextTrim("depart");
                        company = recordEle.elementTextTrim("company");
                        respon = recordEle.elementTextTrim("responid");
                        zhidan = recordEle.elementTextTrim("responid");
                        quyu = recordEle.elementTextTrim("departid");
                        zuzhi = recordEle.elementTextTrim("companyid");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return username+";"+depart+";"+company;
            }else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!s.equals("")){                            
                String[] str = s.split(";");
                tv_respon.setText(str[0]);
                tv_zhidan.setText(str[0]);
                tv_quyu.setText(str[1]);
                tv_zuzhi.setText(str[2]);
            }
        }
    }
    //查币别和汇率
    class CTask extends AsyncTask<Void,String,String>{
        Spinner sp;

        public CTask(Spinner sp){
            this.sp = sp;
        }
        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "JA_select";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/JA_select";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            String sql = "select fcurrencyid,FName,FExchangeRate from t_Currency where fcurrencyid>0";
            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_user");

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = true;
            // 等价于envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE transport = new HttpTransportSE(endPoint);
            try {
                // 调用WebService
                transport.call(soapAction, envelope);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("AddTaskActivity", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            Log.i("返回结果", object.getProperty(0).toString()+"=========================");
            String result = object.getProperty(0).toString();
            Document doc = null;
            try {
                doc = DocumentHelper.parseText(result); // 将字符串转为XML
                Element rootElt = doc.getRootElement(); // 获取根节点
                System.out.println("CTask的根节点：" + rootElt.getName()); // 拿到根节点的名称
                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    HashMap<String,String> map = new HashMap<>();
                    map.put("fitemid",recordEle.elementTextTrim("fcurrencyid"));
                    map.put("bibie",recordEle.elementTextTrim("FName"));
                    map.put("huilv",df.format(Double.parseDouble(recordEle.elementTextTrim("FExchangeRate"))));
                    list1.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            for(HashMap<String,String> map:list1){
                String fname = map.get("bibie");
                strList1.add(fname);
            }
            SpinnerAdapter adapter1 = new ArrayAdapter<>(AddTaskActivity.this, android.R.layout.simple_spinner_item, strList1);
            sp.setAdapter(adapter1);
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    currencyid = Integer.parseInt(list1.get(i).get("fitemid"));
                    tv_huilv.setText(list1.get(i).get("huilv"));
                    huilv = Double.parseDouble(list1.get(i).get("huilv"));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            Log.i("币别",currency+"<<<<<<<<<<<");
            if(currency!=null){
                for (int i = 0; i < adapter1.getCount(); i++) {
                    Log.i("获得的币别",adapter1.getItem(i)+"");
                    if (currency.equals(adapter1.getItem(i).toString())) {
                        sp.setSelection(i);// 默认选中项
                        break;
                    }
                }
            }
        }
    }
    //查组织机构
    class DepartsTask extends AsyncTask<Void,String,String>{

        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "JA_select";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/JA_select";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            String sql = "select fitemid,fname from t_Item_3001 where fitemid>0";
            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_user");

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = true;
            // 等价于envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE transport = new HttpTransportSE(endPoint);
            try {
                // 调用WebService
                transport.call(soapAction, envelope);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("AddTaskActivity", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            Log.i("返回结果", object.getProperty(0).toString()+"=========================");
            String result = object.getProperty(0).toString();
            Document doc = null;
            try {
                doc = DocumentHelper.parseText(result); // 将字符串转为XML
                Element rootElt = doc.getRootElement(); // 获取根节点
                System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    HashMap<String,String> map = new HashMap<>();
                    map.put("itemid",recordEle.elementTextTrim("fitemid"));
                    map.put("fname",recordEle.elementTextTrim("fname"));
                    list1.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PinyinComparator comparator = new PinyinComparator();
            Collections.sort(list1,comparator);
            for(HashMap<String,String> map:list1){
                String name = map.get("fname");
                strList1.add(name);
            }
            final ListView lv = new ListView(AddTaskActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this,android.R.layout.simple_list_item_1,strList1);
            lv.setAdapter(adapter);
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(lv)
                    .setTitle(R.string.departs).show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    tv_zuzhi.setText(strList1.get(i));
                    zuzhi = list1.get(i).get("itemid");
                    dialog.dismiss();
                }
            });
        }

    }
    //查区域部门
    class AreaTask extends AsyncTask<Void,String,String>{

        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "JA_select";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/JA_select";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            String sql = "select fitemid,fname from t_Department where fitemid>0";
            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_user");

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = true;
            // 等价于envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE transport = new HttpTransportSE(endPoint);
            try {
                // 调用WebService
                transport.call(soapAction, envelope);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("AddTaskActivity", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            Log.i("返回结果", object.getProperty(0).toString()+"=========================");
            String result = object.getProperty(0).toString();
            Document doc = null;
            try {
                doc = DocumentHelper.parseText(result); // 将字符串转为XML
                Element rootElt = doc.getRootElement(); // 获取根节点
                System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    HashMap<String,String> map = new HashMap<>();
                    map.put("itemid",recordEle.elementTextTrim("fitemid"));
                    map.put("fname",recordEle.elementTextTrim("fname"));
                    list1.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PinyinComparator comparator = new PinyinComparator();
            Collections.sort(list1,comparator);
            for(HashMap<String,String> map:list1){
                String name = map.get("fname");
                strList1.add(name);
            }
            final ListView lv = new ListView(AddTaskActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this,android.R.layout.simple_list_item_1,strList1);
            lv.setAdapter(adapter);
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(lv)
                    .setTitle(R.string.area).show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    quyu = list1.get(i).get("itemid");
                    tv_quyu.setText(strList1.get(i));
                    dialog.dismiss();
                }
            });
        }
    }
    //查制度所属部门和制度操作细则
//    class ZhiduTask extends AsyncTask<Void,String,String>{
//
//        @Override
//        protected void onPreExecute() {
//            list1.clear();
//            strList1.clear();
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(Void... voids) {
//            // 命名空间
//            String nameSpace = "http://tempuri.org/";
//            // 调用的方法名称
//            String methodName = "JA_select";
//            // EndPoint
//            String endPoint = Consts.ENDPOINT;
//            // SOAP Action
//            String soapAction = "http://tempuri.org/JA_select";
//
//            // 指定WebService的命名空间和调用的方法名
//            SoapObject rpc = new SoapObject(nameSpace, methodName);
//
//            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
//            String sql = "select fitemid,fname,f_102 from t_Item_3006 where fitemid>0";
//            rpc.addProperty("FSql", sql);
//            rpc.addProperty("FTable", "t_user");
//
//            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
//            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
//
//            envelope.bodyOut = rpc;
//            // 设置是否调用的是dotNet开发的WebService
//            envelope.dotNet = true;
//            // 等价于envelope.bodyOut = rpc;
//            envelope.setOutputSoapObject(rpc);
//
//            HttpTransportSE transport = new HttpTransportSE(endPoint);
//            try {
//                // 调用WebService
//                transport.call(soapAction, envelope);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.i("AddTaskActivity", e.toString() + "==================================");
//            }
//
//            // 获取返回的数据
//            SoapObject object = (SoapObject) envelope.bodyIn;
//
//            // 获取返回的结果
//            Log.i("返回结果", object.getProperty(0).toString()+"=========================");
//            String result = object.getProperty(0).toString();
//            Document doc = null;
//            try {
//                doc = DocumentHelper.parseText(result); // 将字符串转为XML
//                Element rootElt = doc.getRootElement(); // 获取根节点
//                System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
//                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
//                // 遍历head节点
//                while (iter.hasNext()) {
//                    Element recordEle = (Element) iter.next();
//                    HashMap<String,String> map = new HashMap<>();
//                    map.put("itemid",recordEle.elementTextTrim("fitemid"));
//                    map.put("fname",recordEle.elementTextTrim("fname"));
//                    map.put("fnote",recordEle.elementTextTrim("f_102"));
//                    list1.add(map);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return "0";
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            for(HashMap<String,String> map:list1){
//                String name = map.get("fname");
//                strList1.add(name);
//            }
//            final ListView lv = new ListView(AddTaskActivity.this);
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this,android.R.layout.simple_list_item_1,strList1);
//            lv.setAdapter(adapter);
//            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(lv)
//                    .setTitle(R.string.zhidu1).show();
//            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    zhidu1 = list1.get(i).get("itemid");
//                    tv_zhidu1.setText(strList1.get(i));
//                    zhidu2 = list1.get(i).get("fnote");
//                    et_zhidu2.setText(list1.get(i).get("fnote"));
//                    dialog.dismiss();
//                }
//            });
//        }
//
//    }

    //查内容和辅助
    class ItemTask extends AsyncTask<Void,String,String>{
        String name;

        public ItemTask(String name){
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
            progress = CustomProgress.show(AddTaskActivity.this,"加载中...",true,null);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "JA_select";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/JA_select";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            String sql;
            if(TextUtils.isEmpty(name)) {
                sql = "select a.fitemid,a.fname,a.ftaxrate,a.fseccoefficient,a.funitid,b.fname sup,c.fname jiliang from t_icitem a left join t_measureunit b on b.fmeasureunitid=a.fsecunitid left join t_measureunit c on c.fitemid=a.funitid where a.fitemid>0";
            }else{
                sql = "select a.fitemid,a.fname,a.ftaxrate,a.fseccoefficient,a.funitid,b.fname sup,c.fname jiliang from t_icitem a left join t_measureunit b on b.fmeasureunitid=a.fsecunitid left join t_measureunit c on c.fitemid=a.funitid where a.fitemid>0 and a.fname like '%" + name + "%'";
            }
            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_user");

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = true;
            // 等价于envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE transport = new HttpTransportSE(endPoint);
            try {
                // 调用WebService
                transport.call(soapAction, envelope);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("AddTaskActivity", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            Log.i("返回结果", object.getProperty(0).toString()+"=========================");
            String result = object.getProperty(0).toString();
            Document doc = null;
            try {
                doc = DocumentHelper.parseText(result); // 将字符串转为XML
                Element rootElt = doc.getRootElement(); // 获取根节点
                System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    HashMap<String,String> map = new HashMap<>();
                    map.put("itemid",recordEle.elementTextTrim("fitemid"));
                    map.put("fname",recordEle.elementTextTrim("fname"));
                    map.put("sup",recordEle.elementTextTrim("sup"));
                    map.put("taxrate",recordEle.elementTextTrim("ftaxrate"));
                    map.put("seccoefficient",recordEle.elementTextTrim("fseccoefficient"));
                    map.put("unitid",recordEle.elementTextTrim("funitid"));
                    map.put("jiliang",recordEle.elementTextTrim("jiliang"));
                    list1.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PinyinComparator comparator = new PinyinComparator();
            Collections.sort(list1,comparator);
            progress.dismiss();
            for(HashMap<String,String> map:list1){
                String name = map.get("fname");
                strList1.add(name);
            }
            final ListView lv = new ListView(AddTaskActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this,android.R.layout.simple_list_item_1,strList1);
            lv.setAdapter(adapter);
//            lv.setPadding(60,20,40,10);
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(lv)
                    .setTitle(R.string.content).show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    contentid = list1.get(i).get("itemid");
                    content = strList1.get(i);
                    sup = list1.get(i).get("sup");
                    jiliangid = list1.get(i).get("unitid");
                    jiliang = list1.get(i).get("jiliang");
                    taxrate = Double.parseDouble(list1.get(i).get("taxrate"))/100;
                    seccoefficient = Double.parseDouble(list1.get(i).get("seccoefficient"));
                    tv_content.setText(content);
                    tv_jl.setText(jiliang);
                    dialog.dismiss();
                }
            });
        }
    }
    //查人员
    class EmpTask extends AsyncTask<Void,String,String>{
        int type;
        String name;

        public EmpTask(int type,String name){
            this.type = type;
            this.name = name;
        }
        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
            progress = CustomProgress.show(AddTaskActivity.this,"加载中",true,null);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "JA_select";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/JA_select";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            String sql;
            if(TextUtils.isEmpty(name)){
                sql = "select fitemid,fname from t_Emp where fitemid>0";
            }else{
                sql = "select fitemid,fname from t_Emp where fitemid>0 and fname like '%" + name + "%'";
            }
            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_user");

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = true;
            // 等价于envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE transport = new HttpTransportSE(endPoint);
            try {
                // 调用WebService
                transport.call(soapAction, envelope);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("AddTaskActivity", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            Log.i("返回结果", object.getProperty(0).toString()+"=========================");
            String result = object.getProperty(0).toString();
            Document doc = null;
            try {
                doc = DocumentHelper.parseText(result); // 将字符串转为XML
                Element rootElt = doc.getRootElement(); // 获取根节点
                System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    HashMap<String,String> map = new HashMap<>();
                    map.put("itemid",recordEle.elementTextTrim("fitemid"));
                    map.put("fname",recordEle.elementTextTrim("fname"));
                    list1.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PinyinComparator comparator = new PinyinComparator();
            Collections.sort(list1,comparator);
            progress.dismiss();
            for(HashMap<String,String> map:list1){
                String name = map.get("fname");
                strList1.add(name);
            }
            final ListView lv = new ListView(AddTaskActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this,android.R.layout.simple_list_item_1,strList1);
            lv.setAdapter(adapter);
//            lv.setPadding(60,20,40,10);
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(lv)
                    .setTitle(R.string.emp).show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    switch(type){
                        case 0:
                            respon = list1.get(i).get("itemid");
                            tv_respon.setText(strList1.get(i));
                            break;
                        case 1:
                            zhidan = list1.get(i).get("itemid");
                            tv_zhidan.setText(strList1.get(i));
                            break;
                        case 2:
                            contacts = list1.get(i).get("itemid");
                            tv_contacts.setText(strList1.get(i));
                            break;
                    }
                    dialog.dismiss();
                }
            });
        }
    }
    //查计量
    class JLTask extends AsyncTask<Void,String,String>{

        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "JA_select";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/JA_select";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            String sql = "select fitemid,fname from t_MeasureUnit where fitemid>0";
            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_user");

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = true;
            // 等价于envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE transport = new HttpTransportSE(endPoint);
            try {
                // 调用WebService
                transport.call(soapAction, envelope);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("AddTaskActivity", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            Log.i("返回结果", object.getProperty(0).toString()+"=========================");
            String result = object.getProperty(0).toString();
            Document doc = null;
            try {
                doc = DocumentHelper.parseText(result); // 将字符串转为XML
                Element rootElt = doc.getRootElement(); // 获取根节点
                System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    HashMap<String,String> map = new HashMap<>();
                    map.put("itemid",recordEle.elementTextTrim("fitemid"));
                    map.put("fname",recordEle.elementTextTrim("fname"));
                    list1.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PinyinComparator comparator = new PinyinComparator();
            Collections.sort(list1,comparator);
            for(HashMap<String,String> map:list1){
                String name = map.get("fname");
                strList1.add(name);
            }
            final ListView lv = new ListView(AddTaskActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this,android.R.layout.simple_list_item_1,strList1);
            lv.setAdapter(adapter);
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(lv)
                    .setTitle(R.string.progress).show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    jiliangid = list1.get(i).get("itemid");
                    jiliang = strList1.get(i);
                    dialog.dismiss();
                }
            });
        }
    }
    //查计划相关字段
    class JHTask extends AsyncTask<Void,String,String>{
        TextView tv,tv1,tv2,tv3;
        String name;

        public JHTask(TextView tv,TextView tv1,TextView tv2,TextView tv3,String name){
            this.tv = tv;
            this.tv1 = tv1;
            this.tv2 = tv2;
            this.tv3 = tv3;
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "JA_select";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/JA_select";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            String sql;
            if(TextUtils.isEmpty(name)) {
                sql = "select a.fitemid,a.fname,a.f_111,a.f_107,b.fname yusuan from t_Item_3007 a left join t_item b on b.fitemid=a.f_105 where a.fitemid>0";
            }else {
                sql = "select a.fitemid,a.fname,a.f_111,a.f_107,b.fname yusuan from t_Item_3007 a left join t_item b on b.fitemid=a.f_105 where a.fitemid>0 and a.fname like '%" + name + "%'";
            }
            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_user");

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = true;
            // 等价于envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE transport = new HttpTransportSE(endPoint);
            try {
                // 调用WebService
                transport.call(soapAction, envelope);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("AddTaskActivity", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            Log.i("返回结果", object.getProperty(0).toString()+"=========================");
            String result = object.getProperty(0).toString();
            Document doc = null;
            try {
                doc = DocumentHelper.parseText(result); // 将字符串转为XML
                Element rootElt = doc.getRootElement(); // 获取根节点
                System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    HashMap<String,String> map = new HashMap<>();
                    map.put("itemid",recordEle.elementTextTrim("fitemid"));
                    map.put("fname",recordEle.elementTextTrim("fname"));
                    map.put("jihua",recordEle.elementTextTrim("f_111"));
                    map.put("jhys",recordEle.elementTextTrim("f_107"));
                    map.put("yusuan",recordEle.elementTextTrim("yusuan"));
                    list1.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PinyinComparator comparator = new PinyinComparator();
            Collections.sort(list1,comparator);
            for(HashMap<String,String> map:list1){
                String name = map.get("fname");
                strList1.add(name);
            }
            final ListView lv = new ListView(AddTaskActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this,android.R.layout.simple_list_item_1,strList1);
            lv.setAdapter(adapter);
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(lv)
                    .setTitle(R.string.progress).show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    planid = list1.get(i).get("itemid");
                    tv.setText(strList1.get(i));
                    tv1.setText(list1.get(i).get("jihua"));
                    tv2.setText(list1.get(i).get("jhys"));
                    tv3.setText(list1.get(i).get("yusuan"));
                    dialog.dismiss();
                }
            });
        }
    }
    //查评分规则
    class PFTask extends AsyncTask<Void,String,String>{
        TextView tv;

        public PFTask(TextView tv){
            this.tv = tv;
        }

        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "JA_select";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/JA_select";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            String sql = "select fitemid,fname from t_Item where FItemClassID=3010";
            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_user");

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = true;
            // 等价于envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE transport = new HttpTransportSE(endPoint);
            try {
                // 调用WebService
                transport.call(soapAction, envelope);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("AddTaskActivity", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            Log.i("返回结果", object.getProperty(0).toString()+"=========================");
            String result = object.getProperty(0).toString();
            Document doc = null;
            try {
                doc = DocumentHelper.parseText(result); // 将字符串转为XML
                Element rootElt = doc.getRootElement(); // 获取根节点
                System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    HashMap<String,String> map = new HashMap<>();
                    map.put("itemid",recordEle.elementTextTrim("fitemid"));
                    map.put("fname",recordEle.elementTextTrim("fname"));
                    list1.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            for(HashMap<String,String> map:list1){
                String name = map.get("fname");
                strList1.add(name);
            }
            final GridView gv = new GridView(AddTaskActivity.this);
            gv.setNumColumns(3);
            final String[] fen = new String[strList1.size()];
            for(int i=0;i<strList1.size();i++){
                fen[i] = strList1.get(i);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this,android.R.layout.simple_list_item_1,
                    fen);
            gv.setAdapter(adapter);
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(gv).show();
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    pfid = list1.get(i).get("itemid");
                    tv.setText(fen[i]);
                    dialog.dismiss();
                }
            });
        }
    }
    //查单子主表详情
    class DeTask extends AsyncTask<Void,String,String> {
        String Taskno;

        public DeTask(String Taskno) {
            this.Taskno = Taskno;
        }

        @Override
        protected void onPreExecute() {
            list.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "JA_select";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/JA_select";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            String sql = "select top 1 a.FAmount4 rate,c.fname departs,c.fitemid departsid,d.fname area,d.fitemid areaid,e.fname currency,e.fcurrencyid," +
                    "f.fname respon,f.fitemid responid,g.fname wanglai,g.fitemid wanglid,h.fname neirong,h.fitemid neirongid,h.ftaxrate,h.fseccoefficient,i.fname zhidan,i.fitemid zhidanid,j.fname zhidu1,j.fitemid zhiduid,a.fnote1,k.fname jiliang,k.fitemid jiliangid from t_BOS200000000 a " +
                    "left join t_BOS200000000Entry2 b on b.FID=a.FID left join t_Item_3001 c " +
                    "on c.FItemID=a.FBase11 left join t_Department d on d.FItemID=a.FBase12 left join" +
                    " t_Currency e on e.FCurrencyID=a.FBase3 left join t_emp f on f.fitemid=b.fbase4 left join" +
                    " t_emp g on g.fitemid=b.fbase10 left join t_ICItem h on h.FItemID=b.FBase1 left join t_emp i on i.fitemid=b.fbase15 left join t_Item_3006 j on j.FItemID=a.FBase13 left join t_measureunit k on k.fitemid=b.fbase2 " +
                    "where a.FBillNo ='" + Taskno + "'";
            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_user");

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = true;
            // 等价于envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE transport = new HttpTransportSE(endPoint);
            try {
                // 调用WebService
                transport.call(soapAction, envelope);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("AddTaskActivity", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            Log.i("返回结果", object.getProperty(0).toString() + "=========================");
            String result = object.getProperty(0).toString();
            Document doc = null;
            try {
                doc = DocumentHelper.parseText(result); // 将字符串转为XML
                Element rootElt = doc.getRootElement(); // 获取根节点
                System.out.println("DeTask的根节点：" + rootElt.getName()); // 拿到根节点的名称
                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("rate", recordEle.elementTextTrim("rate"));
                    map.put("departs", recordEle.elementTextTrim("departs"));
                    map.put("departsid", recordEle.elementTextTrim("departsid"));
                    map.put("currency", recordEle.elementTextTrim("currency"));
                    map.put("fcurrencyid", recordEle.elementTextTrim("fcurrencyid"));
                    if(recordEle.elementTextTrim("area").equals("")){
                        map.put("area","");
                        map.put("areaid",null);
                    }else {
                        map.put("area", recordEle.elementTextTrim("area"));
                        map.put("areaid", recordEle.elementTextTrim("areaid"));
                    }
                    map.put("neirong", recordEle.elementTextTrim("neirong"));
                    map.put("neirongid", recordEle.elementTextTrim("neirongid"));
                    map.put("respon", recordEle.elementTextTrim("respon"));
                    map.put("responid", recordEle.elementTextTrim("responid"));
                    if(recordEle.elementTextTrim("zhidan").equals("")){
                        map.put("zhidan","");
                        map.put("zhidanid",null);
                    }else {
                        map.put("zhidan", recordEle.elementTextTrim("zhidan"));
                        map.put("zhidanid", recordEle.elementTextTrim("zhidanid"));
                    }
                    map.put("wanglai", recordEle.elementTextTrim("wanglai"));
                    map.put("wanglid", recordEle.elementTextTrim("wanglid"));
                    map.put("zhidu1", recordEle.elementTextTrim("zhidu1"));
                    map.put("zhiduid", recordEle.elementTextTrim("zhiduid"));
                    map.put("fnote1", recordEle.elementTextTrim("fnote1"));
                    map.put("taxrate",recordEle.elementTextTrim("ftaxrate"));
                    map.put("seccoefficient",recordEle.elementTextTrim("fseccoefficient"));
                    map.put("jiliang",recordEle.elementTextTrim("jiliang"));
                    map.put("jiliangid",recordEle.elementTextTrim("jiliangid"));
                    list.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            if (list.get(0).get("fcurrencyid").equals("1")) {
//                sp_bibie.setSelection(0);
//            } else {
//                sp_bibie.setSelection(1);
//            }
            tv_huilv.setText(df.format(Double.parseDouble(list.get(0).get("rate"))));
            tv_zuzhi.setText(list.get(0).get("departs"));
            tv_quyu.setText(list.get(0).get("area"));
            tv_content.setText(list.get(0).get("neirong"));
            tv_respon.setText(list.get(0).get("respon"));
            tv_zhidan.setText(list.get(0).get("zhidan"));
            tv_contacts.setText(list.get(0).get("wanglai"));
            tv_jl.setText(list.get(0).get("jiliang"));
            currencyid = Integer.parseInt(list.get(0).get("fcurrencyid"));
            currency = list.get(0).get("currency");
            zuzhi = list.get(0).get("departsid");
            quyu = list.get(0).get("areaid");
            zhidu1 = list.get(0).get("zhiduid");
            zhidu2 = list.get(0).get("fnote1");
            contentid = list.get(0).get("neirongid");
            content = list.get(0).get("neirong");
            respon = list.get(0).get("responid");
            zhidan = list.get(0).get("zhidanid");
            contacts = list.get(0).get("wanglid");
            taxrate = Double.parseDouble(list.get(0).get("taxrate"))/100;
            seccoefficient = Double.parseDouble(list.get(0).get("seccoefficient"));
            jiliangid = list.get(0).get("jiliangid");
        }

    }
    //查询单子子表详情
    class DeEntryTask extends AsyncTask<Void,String,String>{
        String Taskno;

        DeEntryTask(String Taskno){
            this.Taskno = Taskno;
        }

        @Override
        protected void onPreExecute() {
            ziList.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "JA_select";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/JA_select";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            String sql = " select b.FTime qi,b.FTime1 zhi,g.FName respon,h.FName progress,h.fitemid planid,h.F_111 plans,i.FName budget,h.f_107 pbudget,b.FNOTE note," +
                    "   j.FName contacts,k.FName neirong,l.FName jiliang,l.fitemid jiliangid,b.FDecimal shuliang,b.FDecimal1 danjia,b.FAmount2 hanshui,b.FAmount3 buhan," +
                    "   b.FText fasong,b.FText1 huikui,o.FName pingfen,o.fitemid pfid,p.FName js1,b.FCheckBox1 qr1,q.FName js2,b.FCheckBox2 qr2," +
                    "   m.FName js3,b.FCheckBox3 qr3,n.FName js4,b.FCheckBox4 qr4,r.FName js5,b.FCheckBox5 qr5,s.fname fuzhu,b.fdecimal2 fuliang" +
                    "    from t_BOS200000000 a inner join t_BOS200000000Entry2 b on a.FID=b.FID" +
                    "   left join t_Currency c on c.FCurrencyID=a.FBase3 left join t_Item_3001 d on d.FItemID=a.FBase11" +
                    "   left join t_Department e on e.FItemID=a.FBase11 left join t_Item_3006 f on f.FItemID=a.FBase13" +
                    "   left join t_Emp g on g.FItemID=b.FBase4 left join t_Item_3007 h on h.FItemID=b.FBase left join" +
                    "   t_item i on i.FItemID=h.F_105 left join t_Emp j on j.FItemID=b.FBase10 left join t_ICItem k on k.FItemID=b.FBase1" +
                    "   left join t_MeasureUnit l on l.FMeasureUnitID=b.FBase2 left join t_Item o on o.FItemID=b.FBase14" +
                    "   left join t_Emp p on p.FItemID=b.FBase5 left join t_Emp q on q.FItemID=b.FBase6" +
                    "   left join t_Emp m on m.FItemID=b.FBase7 left join t_Emp n on n.FItemID=b.fbase8" +
                    "   left join t_Emp r on r.FItemID=b.FBase9 left join t_MeasureUnit s on s.FMeasureUnitID=k.FSecUnitID where a.fbillno='"+Taskno+"'";
            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_BOS200000000");

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = true;
            // 等价于envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE transport = new HttpTransportSE(endPoint);
            try {
                // 调用WebService
                transport.call(soapAction, envelope);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("MeFragment", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            Log.i("返回结果", object.getProperty(0).toString()+"=========================");
            String result = object.getProperty(0).toString();
            Document doc = null;

            try {
                doc = DocumentHelper.parseText(result); // 将字符串转为XML

                Element rootElt = doc.getRootElement(); // 获取根节点

                System.out.println("DeEntryTask的根节点：" + rootElt.getName()); // 拿到根节点的名称

                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head

                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    planid = recordEle.elementTextTrim("planid");
//                    jiliangid = recordEle.elementTextTrim("jiliangid");
                    pfid = recordEle.elementTextTrim("pfid");
                    String qi = recordEle.elementTextTrim("qi");
                    String zhi = recordEle.elementTextTrim("zhi");
                    String neirong = recordEle.elementTextTrim("neirong");
                    String jiliang = recordEle.elementTextTrim("jiliang");
                    String shuliang = recordEle.elementTextTrim("shuliang");
                    String danjia = recordEle.elementTextTrim("danjia");
                    String progress = recordEle.elementTextTrim("progress");
                    String plan = recordEle.elementTextTrim("plans");
                    String budget = recordEle.elementTextTrim("budget");
                    String pbudget = recordEle.elementTextTrim("pbudget");
                    String note = recordEle.elementTextTrim("note");
                    String hanshui = recordEle.elementTextTrim("hanshui");
                    String buhan = recordEle.elementTextTrim("buhan");
                    String fuzhu = recordEle.elementTextTrim("fuzhu");
                    String fuliang = recordEle.elementTextTrim("fuliang");
                    String fasong = recordEle.elementTextTrim("fasong");
                    String huikui = recordEle.elementTextTrim("huikui");
                    String pingfen = recordEle.elementTextTrim("pingfen");
                    HashMap<String,String> map = new HashMap<>();
                    map.put("qi",qi);
                    map.put("zhi",zhi);
                    map.put("neirong",neirong);
//                    map.put("jiliang",jiliang);
                    map.put("shuliang",df.format(Double.parseDouble(shuliang)));
                    map.put("danjia",df.format(Double.parseDouble(danjia)));
                    map.put("progress",progress);
                    map.put("plan",plan);
                    map.put("budget",budget);
                    map.put("pbudget",df.format(Double.parseDouble(pbudget)));
                    map.put("note",note);
                    map.put("hanshui",df.format(Double.parseDouble(hanshui)));
                    map.put("buhan",df.format(Double.parseDouble(buhan)));
                    map.put("fuzhu",fuzhu);
                    map.put("fuliang",df.format(Double.parseDouble(fuliang)));
                    map.put("fasong",fasong);
                    map.put("huikui",huikui);
                    map.put("pingfen",pingfen);
                    map.put("planid",planid);
//                    map.put("jiliangid",jiliangid);
                    map.put("pfid",pfid);
                    ziList.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(list.size()==0){
                return "0";
            }else {
                return "1";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter = new ZiAdapter(AddTaskActivity.this, ziList);
            lv_zb.setAdapter(adapter);
        }
    }
}
