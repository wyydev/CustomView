package cn.kinglian.www.customview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;

import cn.kinglian.www.customview.bezier.Bezier2;
import cn.kinglian.www.customview.bezier.Bezier4;

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
}
