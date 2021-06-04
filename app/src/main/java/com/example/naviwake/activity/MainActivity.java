package com.example.naviwake.activity;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.example.naviwake.model.Pos;
import com.example.naviwake.R;
import com.example.naviwake.util.FucUtil;
import com.example.naviwake.util.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends CheckPermissionsActivity {
    ArrayList<Pos> posArrayList=new ArrayList<>();
    private static final String TAG = "MainActivity";
    //private static final double EARTH_RADIUS = 6378.137;
    public MediaPlayer questionPlayer;
    public MediaPlayer mMediaPlayer;
    public MediaPlayer noposplayer;
    public MediaPlayer ansplayer;
    private Toast mToast;
    private TextView textView;
    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    // 语音识别对象
    private SpeechRecognizer mAsr;
    // 唤醒结果内容
    private String resultString;
    // 本地语法id
    private String mLocalGrammarID=null;
    private int curThresh = 1450;
    // 本地语法文件
    private String mLocalGrammar = null;
    // 本地语法构建路径
    private String grmPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/msc/test";
    private String grmPath0 = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/msc/test0";
    private String grmPath1 = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/msc/test1";
    private String grmPath2 = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/msc/test2";
    private String grmPath3 = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/msc/test3";
    private String grmPath4 = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/msc/test4";
    private String grmPath5 = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/msc/test5";
    //File file = new File(grmPath);
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_LOCAL;

    //初始化音频管理器
    private AudioManager mAudioManager;
    private TextView lati;
    private TextView lngi;
    private TextView mindis;
    private TextView jindian;
    private ArrayList<Pos> posList=new ArrayList<>();
    ArrayList<Double> temps=new ArrayList<>();
    //ArrayList<Thread> threads=new ArrayList<>();
    private Pos[] poses={
            //室内测试
            //new Pos("景点1",121.772285,39.047214,30),
            //楼下
            new Pos("景点1",121.772805,39.047098,30),
            new Pos("景点2",121.772786,39.043913,30),
            //食堂之间
            //new Pos("景点3",121.775394,39.043943,30),
            //生命院
            new Pos("景点3",121.774583,39.047187,30)
    };
    public ArrayList<Pos> Getpos(){
        for (Pos pos:poses){
            posArrayList.add(pos);
        }
        return posArrayList;
    };
    double car_longitude;
    double car_latitude;
    public String name;
    public String lastname;
    public String filename;
    public int bg_number=0;
    public int flag=1;
    //存储问题序号
    public int ques_number;
    ExecutorService exec;
    //保持问题入队列
    Queue<Integer> queue=new LinkedList<Integer>();
    private TextView tvResult;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化view
        initview();
        //初始化四个mediaPlayer
        initmediaPlayer();
        //初始化启动定位
        initlocation();
        //线程池
        exec = Executors.newSingleThreadExecutor();
        Getpos();
        // 初始化唤醒对象
        mIvw = VoiceWakeuper.createWakeuper(this, null);
        // 初始化识别对象---唤醒+识别,用来构建语法
        mAsr = SpeechRecognizer.createRecognizer(this, mInitListener);
        initgrammar();
        initdata();
    }

    /**
     * 定位监听
     */
    AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if(location.getErrorCode() == 0){
                    sb.append("定位类型: ").append(location.getLocationType()).append("\n");
                    sb.append("经    度    : ").append(location.getLongitude()).append("\n");
                    sb.append("纬    度    : ").append(location.getLatitude()).append("\n");
                    sb.append("精    度    : ").append(location.getAccuracy()).append("米").append("\n");
                    sb.append("提供者    : ").append(location.getProvider()).append("\n");
                    sb.append("速    度    : ").append(location.getSpeed()).append("米/秒").append("\n");
                    sb.append("角    度    : ").append(location.getBearing()).append("\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : ").append(location.getSatellites()).append("\n");
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:").append(location.getErrorCode()).append("\n");
                    sb.append("错误信息:").append(location.getErrorInfo()).append("\n");
                    sb.append("错误描述:").append(location.getLocationDetail()).append("\n");
                }
                sb.append("***定位质量报告***").append("\n");
                sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启":"关闭").append("\n");
                sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
                sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
                sb.append("* 网络类型：").append(location.getLocationQualityReport().getNetworkType()).append("\n");
                //解析定位结果，
                String result = sb.toString();
                tvResult.setText(result);

                car_latitude=location.getLatitude();
                car_longitude=location.getLongitude();

                function();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lati.setText(String.valueOf(car_latitude));
                                lngi.setText(String.valueOf(car_longitude));
                                mindis.setText(minNum_str);
                                jindian.setText(jindian1);
                            }
                        });
                    }
                }).start();
                posList.clear();
                temps.clear();
            } else {
                tvResult.setText("定位失败，loc is null");
            }
        }
    };
    /**
     * 获取GPS状态的字符串
     * @param statusCode GPS状态码
     * @return
     */
    private String getGPSStatusString(int statusCode){
        String str = "";
        switch (statusCode){
            case AMapLocationQualityReport.GPS_STATUS_OK:
                str = "GPS状态正常";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                str = "手机中没有GPS Provider，无法进行GPS定位";
                break;
            case AMapLocationQualityReport.GPS_STATUS_OFF:
                str = "GPS关闭，建议开启GPS，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
                str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                str = "没有GPS定位权限，建议开启gps定位权限";
                break;
        }
        return str;
    }



    String minNum_str;
    String jindian1;
    //一直在景点内，allmusicflag=1不播放景点音乐，达到景点音乐只播放一次的目的
    //int allmusicflag=0;
    public void function(){
        posList=GetposArrayList(car_latitude);
        for(int i=0;i<posList.size();i++){
            double distance= FucUtil.getDistance(car_longitude,car_latitude,posList.get(i).getLongitude(),posList.get(i).getLatitude());
            temps.add(i,distance);
            double minNum = Collections.min(temps);
            //int place=temps.indexOf(minNum);
            minNum_str=String.format("%.3f",minNum);
            Log.e("distance_min",minNum_str+"km");
            if(distance*1000<=posList.get(i).getRadius()){
                //&&allmusicflag==0
                if (!mMediaPlayer.isPlaying()&&(!questionPlayer.isPlaying())&&(!ansplayer.isPlaying())&&mdflag==0) {
                    //轮询才可使用
                    //posArrayList.remove(i);
                    if(noposplayer.isPlaying())
                        noposplayer.pause();
                    //将暂停音乐名称存在lastname
                    lastname=name;
                    flag=0;
                    filename = "/storage/emulated/0/";
                    //filename=Environment.getExternalStorageDirectory().getAbsolutePath();
                    Log.e("distance_min",filename);
                    name = posList.get(i).getName();
                    try {
                        mMediaPlayer.reset();
                        filename = filename + name + ".mp3";
                        Log.e("filename", filename);
                        mMediaPlayer.setDataSource(filename);
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                        mMediaPlayer.setVolume(1f, 1f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                jindian1="景点内";
                //allmusicflag=1;
                return;
            }
            else{
                if((!mMediaPlayer.isPlaying())&&(!noposplayer.isPlaying())&&(!questionPlayer.isPlaying())&&(!ansplayer.isPlaying())&&flag==0) {
                    //allmusicflag=0;
                    noposplayer.start();
                    name=lastname;
                    flag=1;
                    Log.e("1111111111", "111111111111111111");
                }
                if((!mMediaPlayer.isPlaying())&&(!noposplayer.isPlaying())&&(!questionPlayer.isPlaying())&&(!ansplayer.isPlaying())&&mdflag==0&&nopflag==0)
                {
                    //allmusicflag=0;
                    String bgfilename = "/storage/emulated/0/bg/";
                    try {
                        noposplayer.reset();
                        name="b"+String.valueOf(bg_number);
                        bgfilename = bgfilename + name + ".mp3";
                        Log.e("bgfilename", bgfilename);
                        queue.offer(bg_number);

                        noposplayer.setDataSource(bgfilename);
                        noposplayer.prepare();
                        noposplayer.start();
                        noposplayer.setVolume(1f, 1f);
                        noposplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                exec.execute(new ThreadShow());
                            }
                        });
                        if(bg_number<5)
                        { bg_number++;}
                        else
                        {bg_number=0;}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                jindian1="景点外";
            }

        }
    }

    public ArrayList<Pos> GetposArrayList(Double latitude){
        ArrayList<Pos> posLists=new ArrayList<>();
        for(int i=0;i<posArrayList.size();i++) {
            if (latitude - 0.05 <posArrayList.get(i).getLatitude() && posArrayList.get(i).getLatitude() < latitude + 0.05) {
                posLists.add(posArrayList.get(i));
            }
        }
        return posLists;
    }
    //提问时把noposplayer或者mMediaPlayer暂停
    private  int nopflag=0;
    private  int mdflag=0;
    //解决问题播放后
    private  int mMediaPlayerflag=0;
    // handler类接收数据
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                ques_number=queue.poll();
                questionPlayer.reset();
                String filename = "/storage/emulated/0/dynasty";
                filename=filename+ques_number+".mp3";
                Log.e("TAG1",String.valueOf(ques_number));
                //先进入景点，则问题延迟
                /*if(mMediaPlayer.isPlaying()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }*/
                try {
                    if(noposplayer.isPlaying())
                    {
                        noposplayer.pause();
                        nopflag=1;
                    }
                    //问题已经播放，则景点延迟
                    if(mMediaPlayer.isPlaying())
                    {
                        mMediaPlayer.pause();
                        mdflag=1;
                    }
                    questionPlayer.setDataSource(filename); // 指定音频文件的路径/storage/emulated/0/music.mp3
                    //mediaPlayer.setLooping(true);//设置为循环播放
                    questionPlayer.prepare(); // 让MediaPlayer进入到准备状态
                    questionPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                questionPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        setgram();
                    }
                });
            }
        };
    };

    // 线程类
    class ThreadShow implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                Thread.sleep(12000);
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    GrammarListener grammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if (error == null) {
                mLocalGrammarID = grammarId;
                showTip("语法构建成功：" + grammarId);
                Log.e("gram","mLocalGrammarID");
            } else {
                showTip("语法构建失败,错误码：" + error.getErrorCode()+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }
    };

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码："+code+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }
    };
    public int errocode;
    /**
     * 识别监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.e("TAG", "返回音频数据："+data.length);
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (null != result && !TextUtils.isEmpty(result.getResultString())) {
                Log.e("TAG", "recognizer result：" + result.getResultString());
                //recoString = JsonParser.parseGrammarResult(result.getResultString());
                int recoint = JsonParser.parseGrammarResultcontact(result.getResultString());
                Log.e("TAG", " "+recoint);
                if(recoint>30)
                {
                    ansplayer= MediaPlayer.create(getApplicationContext(), R.raw.dui);
                    ansplayer.start();
                    ansplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            musicrestart();
                        }
                    });
                    textView.setText("答对了");
                }
                else {
                    ansplayer= MediaPlayer.create(getApplicationContext(), R.raw.cuo);
                    ansplayer.start();
                    ansplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            musicrestart();
                        }
                    });
                    textView.setText("您好，没听到你说的答案，不好意思");
                    Log.e("TAG", "recognizer result : null");
                }
                //musicrestart();
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
            Log.e("TAG1", "end");
            //FucUtil.deleteDirWihtFile(file);
            //mediaPlayer.start(); // 开始播放
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            if(error.getErrorCode()==20005&&recogflag<5){
                recogflag++;
                Log.e("TAG1", "次数"	+ recogflag);
                mAsr.startListening(mRecognizerListener);
            }
            else {
                textView.setText("您好，没听到你说的答案，不好意思");
                ansplayer = MediaPlayer.create(getApplicationContext(), R.raw.tingbuqing);
                ansplayer.start();
                ansplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        musicrestart();
                    }
                });
            }
                // musicrestart();
            errocode=error.getErrorCode();
                showTip("onError Code："	+ error.getErrorCode());
                Log.e("TAG1", "onError Code："	+ error.getErrorCode());
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            Log.e("TAG1", "event");
        }

    };

    /**
     * 识别监听器。
     */
    private RecognizerListener quesRecognizerListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (null != result && !TextUtils.isEmpty(result.getResultString())) {
                Log.d(TAG, "recognizer result：" + result.getResultString());
                //recoString = JsonParser.parseGrammarResult(result.getResultString());
                int contact = JsonParser.parseGrammarResultcontact(result.getResultString());
                int callCmd = JsonParser.parseGrammarResultcallCmd(result.getResultString());
                int contactmin = JsonParser.parseGrammarResultcontactmin(result.getResultString());
                int callCmdmax = JsonParser.parseGrammarResultcallCmdmax(result.getResultString());
                Log.d(TAG, " "+contact);
                Log.d(TAG, " "+callCmd);
                if(contact>30)
                    //减少音量
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,AudioManager.FX_FOCUS_NAVIGATION_UP);
                if(callCmd>30)
                    //增加电量
                    mAudioManager.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,AudioManager.FX_FOCUS_NAVIGATION_UP);
                if(contactmin>30)
                    //减少音量
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,AudioManager.FX_FOCUS_NAVIGATION_UP);
                if(callCmdmax>30)
                    //增加电量
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),AudioManager.FX_FOCUS_NAVIGATION_UP);
                if(callCmd<=30&&contact<=30&&callCmdmax<=30&&contactmin<=30)
                    textView.setText("不好意思,没有听懂你的意思");
                musicrestart();
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
            //FucUtil.deleteDirWihtFile(file);
            //mediaPlayer.start(); // 开始播放
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            textView.setText("不好意思,没有听懂你的意思");
            musicrestart();
            showTip("onError Code："	+ error.getErrorCode());
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null

        }

    };

    //说出命令词后，启动之前播放
    private void musicrestart(){
        if((!mMediaPlayer.isPlaying())&&(!noposplayer.isPlaying())&&(!questionPlayer.isPlaying())&&mdflag==1){
            mMediaPlayer.start();
            mdflag=0;
        }
        if((!mMediaPlayer.isPlaying())&&(!noposplayer.isPlaying())&&(!questionPlayer.isPlaying())&&nopflag==1){
            noposplayer.start();
            nopflag=0;
        }
    }

    int recogflag;
    //设置判断回答正错的识别
    public void setgram(){
        String dynasty="dynasty"+ques_number;
        String gram="grmPath"+ques_number;
        mLocalGrammar = FucUtil.readFile(MainActivity.this, dynasty+".bnf", "utf-8");
        Log.e("dyna",dynasty);
        mAsr.setParameter(SpeechConstant.LOCAL_GRAMMAR, dynasty);
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, gram);
        mAsr.buildGrammar("bnf",mLocalGrammar, grammarListener);
        recogflag=1;
        mAsr.startListening(mRecognizerListener);
    }

    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            if(noposplayer.isPlaying())
            {
                noposplayer.pause();
                nopflag=1;
            }
            if(mMediaPlayer.isPlaying())
            {
                mMediaPlayer.pause();
                mdflag=1;
            }
            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【RAW】 "+text);
                buffer.append("\n");
                buffer.append("【操作类型】"+ object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】"+ object.optString("id"));
                buffer.append("\n");
                buffer.append("【得分】" + object.optString("score"));
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                resultString =buffer.toString();
            } catch (JSONException e) {
                resultString = "结果解析出错";
                e.printStackTrace();
            }
            //为了解决多次唤醒后，一次识别出现多次同一唤醒词
            if(mAsr!=null)
            {
                mAsr.stopListening();
            }
            textView.setText(resultString);

            mLocalGrammar = FucUtil.readFile(MainActivity.this, "voiceadjust.bnf", "utf-8");
            mAsr.setParameter(SpeechConstant.LOCAL_GRAMMAR, "voiceadjust");
            mAsr.buildGrammar("bnf", mLocalGrammar, grammarListener);
            mAsr.startListening(quesRecognizerListener);
        }

        @Override
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }


        @Override
        public void onBeginOfSpeech() {
            showTip("开始说话");
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
            Log.d(TAG, "eventType:"+eventType+ "arg1:"+isLast + "arg2:" + arg2);
        }

        @Override
        public void onVolumeChanged(int volume) {
            // TODO Auto-generated method stub
        }

    };

    private void initmediaPlayer(){
        //播放问题语音
        questionPlayer = new MediaPlayer();
        //回答问题后语音
        ansplayer= new MediaPlayer();
        mMediaPlayer=new MediaPlayer();
        noposplayer=new MediaPlayer();
        //初始化音频管理器
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    }
    private void initlocation(){
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption = getDefaultOption();
        //启动定位
        mLocationClient.startLocation();
    }
    /**
     * 默认的定位参数
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }
    private void initview(){
        tvResult = (TextView) findViewById(R.id.tv_result);
        lati=(TextView) findViewById(R.id.lat);
        lngi=(TextView) findViewById(R.id.lng);
        mindis=(TextView)findViewById(R.id.dist);
        jindian=(TextView)findViewById(R.id.jindian);
        textView = (TextView) findViewById(R.id.txt_show_msg);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }
    private void initgrammar() {
        mAsr.setParameter(SpeechConstant.PARAMS, null);
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        // 设置引擎类型
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置语法构建路径
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
        // 设置本地识别使用语法id
        //mAsr.setParameter(SpeechConstant.LOCAL_GRAMMAR, "dynasty1");
        // 设置识别的门限值
        // 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
        //取值范围{1000～10000}
        //mAsr.setParameter(SpeechConstant.VAD_ENABLE,"1");
        //mAsr.setParameter(SpeechConstant.VAD_BOS, "10000");
        //设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
        //自动停止录音，范围{0~10000}
        //mAsr.setParameter(SpeechConstant.VAD_EOS, "10000");
        mAsr.setParameter(SpeechConstant.MIXED_THRESHOLD, "30");
        // 设置资源路径
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/asr.wav");
        mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
    }
    private void initdata(){
        // 非空判断，防止因空指针使程序崩溃
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            resultString = "";
            textView.setText(resultString);

            final String resPath = ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "ivw/"+getString(R.string.app_id)+".jet");
            // 清空参数
            mIvw.setParameter(SpeechConstant.PARAMS, null);
            // 设置识别引擎
            //mIvw.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
            // 设置唤醒资源路径
            mIvw.setParameter(ResourceUtil.IVW_RES_PATH, resPath);
            /**
             * 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
             * 示例demo默认设置第一个唤醒词，建议开发者根据定制资源中唤醒词个数进行设置
             */
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"
                    + curThresh);
            // 设置唤醒模式
            mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
            // 设置持续进行唤醒
            mIvw.setParameter(SpeechConstant.KEEP_ALIVE,"1");
//			mIvw.setParameter(SpeechConstant.IVW_SHOT_WORD, "0");
            // 设置唤醒录音保存路径，保存最近一分钟的音频
            mIvw.setParameter( SpeechConstant.IVW_AUDIO_PATH, Environment.getExternalStorageDirectory().getPath()+"/msc/ivw.wav" );
            mIvw.setParameter( SpeechConstant.AUDIO_FORMAT, "wav" );
            mIvw.startListening(mWakeuperListener);
        } else {
            showTip("唤醒未初始化");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMediaPlayer!=null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
        if (noposplayer!=null) {
            noposplayer.stop();
            noposplayer.reset();
            noposplayer.release();
            noposplayer=null;
        }
        if (questionPlayer!=null) {
            questionPlayer.stop();
            questionPlayer.reset();
            questionPlayer.release();
            questionPlayer=null;
        }
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.destroy();
        } else {
            showTip("唤醒未初始化");
        }

        if( null != mAsr ){
            // 退出时释放连接
            mAsr.cancel();
            mAsr.destroy();
        }
        if (null != mLocationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mLocationClient.onDestroy();
            mLocationClient = null;
            mLocationOption = null;
        }
    }

    // 获取识别资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        // 识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this,ResourceUtil.RESOURCE_TYPE.assets, "asr/common.jet"));
        return tempBuffer.toString();
    }

    private void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }
}