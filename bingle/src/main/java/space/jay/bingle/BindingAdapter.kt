package space.jay.bingle

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("onClickMethod")
fun onClickMethod(view: View, method: () -> Unit) {
    //로그인 메소드 넘겨받아 실행
    view.setOnClickListener { method.invoke() }
}