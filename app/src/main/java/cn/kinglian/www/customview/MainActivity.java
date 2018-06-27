package cn.kinglian.www.customview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.Serializable;

/**
 * @author wen
 * @date 2018/6/6
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //showBezier2();

//        showBezier4();

//        initPay();
    }

//    private void showBezier4() {
//        final Bezier4 bezier = findViewById(R.id.bezier);
//        Button btnStart = findViewById(R.id.btn_start);
//        btnStart.setOnClickListener((view) -> {
//            bezier.reset();
//            bezier.move();
//        });
//    }


//    private void showBezier2() {
//        final Bezier2 bezier = findViewById(R.id.bezier);
//        RadioGroup radioGroup = findViewById(R.id.rg_control);
//
//        radioGroup.setOnCheckedChangeListener((RadioGroup group, int checkedId) -> {
//            if (checkedId == R.id.rb_one) {
//                bezier.setControlPoint(Bezier2.POINT_ONE);
//            } else if (checkedId == R.id.rb_two) {
//                bezier.setControlPoint(Bezier2.POINT_TWO);
//            }
//        });
//    }

//    private PayResultView.ResultType resultType;
//
//    private void initPay() {
//        resultType = PayResultView.ResultType.SUCCESS;
//        PayResultView payResultView = findViewById(R.id.pay_result_view);
//        Button btnStart = findViewById(R.id.btn_start);
//        Button btnFinish = findViewById(R.id.btn_finish);
//        btnStart.setOnClickListener((view) -> payResultView.start());
//        btnFinish.setOnClickListener((view) -> payResultView.finish(resultType));
//        RadioGroup radioGroup = findViewById(R.id.rg_control);
//        radioGroup.setOnCheckedChangeListener((RadioGroup group, int checkedId) -> {
//            if (checkedId == R.id.rb_one) {
//                resultType = PayResultView.ResultType.SUCCESS;
//            } else if (checkedId == R.id.rb_two) {
//                resultType = PayResultView.ResultType.FAILED;
//            } else if (checkedId == R.id.rb_three) {
//                resultType = PayResultView.ResultType.WARNING;
//            }
//        });
//    }


    public static final String PRINTER_ID = "print_id";
    public static final String USER_ID = "user_id";
    public static final String PRINTER_PARAM = "print_param";

    /**
     * @param printerId 打印机id
     * @param userId 用户id
     * @param printParam 打印参数
     * @return 使用该Intent启动
     */
    public static Intent startAActivity(int printerId,int userId,PrintParam printParam){
        Intent intent = new Intent();
        intent.putExtra(PRINTER_ID,printerId);
        intent.putExtra(USER_ID,userId);
        intent.putExtra(PRINTER_PARAM,printParam);
        return intent;
    }

    class PrintParam implements Serializable{

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }
}
