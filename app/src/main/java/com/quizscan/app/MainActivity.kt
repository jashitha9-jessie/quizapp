package com.quizscan.app
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.quizscan.app.data.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
class MainActivity:AppCompatActivity(){
 private val auth by lazy{FirebaseAuth.getInstance()}; private val db by lazy{(application as QuizScanApplication).database}
 private val cats=mutableListOf<Category>(); private var catId=9; private var level="easy"; private var catName="General Knowledge"
 private var questions=listOf<TriviaQuestion>(); private var options=listOf<String>(); private var index=0; private var score=0; private var answered=false
 private lateinit var root:LinearLayout; private lateinit var categorySpinner:Spinner; private lateinit var levelSpinner:Spinner
 private val request=72
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContentView(R.layout.activity_main);root=LinearLayout(this).apply{orientation=1;setPadding(24,24,24,24)};findViewById<FrameLayout>(R.id.mainContainer).addView(root);home();loadCategories()}
 private fun home(){root.removeAllViews();heading("QuizScan");text("Join a quiz by scanning a QR code or choose your quiz.",17);button("Continue with Google"){googleSignIn()};button("Scan QR Code"){scan()};text("Category",15);categorySpinner=Spinner(this);root.addView(categorySpinner);text("Difficulty",15);levelSpinner=Spinner(this);levelSpinner.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,listOf("easy","medium","hard"));root.addView(levelSpinner);button("Start Quiz"){if(cats.isNotEmpty()){catId=cats[categorySpinner.selectedItemPosition].id;catName=cats[categorySpinner.selectedItemPosition].name;level=levelSpinner.selectedItem.toString();startQuiz()}else toast("Categories are loading")};button("Score History"){history()};text(auth.currentUser?.let{"Signed in: ${it.displayName?:it.email}"}?:"Sign in to continue with Google",13)}
 private fun loadCategories(){lifecycleScope.launch{runCatching{Api.service.categories().trivia_categories}.onSuccess{cats.clear();cats.addAll(it);categorySpinner.adapter=ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,cats.map{c->c.name})}.onFailure{toast("Could not load categories")}}}
 private fun scan(){GmsBarcodeScanning.getClient(this).startScan().addOnSuccessListener{b->val raw=b.rawValue.orEmpty();val m=Regex("category=(\\d+);difficulty=(easy|medium|hard)",RegexOption.IGNORE_CASE).matchEntire(raw);if(m==null)toast("Invalid QR format")else{catId=m.groupValues[1].toInt();level=m.groupValues[2].lowercase();catName=cats.find{it.id==catId}?.name?:"Category $catId";startQuiz()}}.addOnFailureListener{toast("Scanner error")}}
 private fun startQuiz(){root.removeAllViews();heading("Loading quiz...");lifecycleScope.launch{runCatching{Api.service.questions(category=catId,difficulty=level)}.onSuccess{r->if(r.response_code!=0||r.results.size!=10){home();toast("No questions found. Try another level.");return@onSuccess};questions=r.results;catName=questions.first().category;index=0;score=0;showQuestion()}.onFailure{home();toast("Network error. Try again.")}}}
 private fun showQuestion(){root.removeAllViews();val q=questions[index];heading("Question ${index+1} / 10");val bar=ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal).apply{max=10;progress=index+1};root.addView(bar,LinearLayout.LayoutParams(-1,18));text(decode(q.question),21);val box=LinearLayout(this).apply{orientation=1};root.addView(box);options=(q.incorrect_answers+q.correct_answer).map{decode(it)}.shuffled();var next:Button?=null
 options.forEach{o->val b=Button(this).apply{text=o;isAllCaps=false};box.addView(b,LinearLayout.LayoutParams(-1,-2));b.setOnClickListener{if(answered)return@setOnClickListener;answered=true;options.forEachIndexed{_,v->val child=box.getChildAt(options.indexOf(v)) as Button;child.isEnabled=false;if(v==decode(q.correct_answer))child.setBackgroundColor(Color.rgb(46,155,95))else if(v==o)child.setBackgroundColor(Color.rgb(210,65,65))};if(o==decode(q.correct_answer))score++;next?.isEnabled=true}}
 answered=false;next=button(if(index==9)"Finish" else "Next"){if(index<9){index++;showQuestion()}else result()};next?.isEnabled=false}
 private fun result(){root.removeAllViews();heading("Quiz Completed");text("Your score: $score / 10",28);text("Category: $catName",17);lifecycleScope.launch{db.scores().insert(ScoreEntity(date=SimpleDateFormat("dd MMM yyyy, hh:mm a",Locale.getDefault()).format(Date()),category=catName,score=score))};button("Play Again"){startQuiz()};button("Score History"){history()};button("Home"){home()}}
 private fun history(){root.removeAllViews();heading("Score History");val rv=RecyclerView(this);rv.layoutManager=LinearLayoutManager(this);root.addView(rv,LinearLayout.LayoutParams(-1,0,1f));lifecycleScope.launch{rv.adapter=object:RecyclerView.Adapter<Holder>(){val data=db.scores().all();override fun getItemCount()=data.size;override fun onCreateViewHolder(p:android.view.ViewGroup,t:Int):Holder{val v=TextView(p.context).apply{setPadding(16,20,16,20);textSize=16f};return Holder(v)};override fun onBindViewHolder(h:Holder,p:Int){val d=data[p];(h.itemView as TextView).text="${d.date}  •  ${d.category}  •  ${d.score}/10"}}};button("Back"){home()}}
 class Holder(v:View):RecyclerView.ViewHolder(v)
 private fun googleSignIn(){val id=resources.getIdentifier("default_web_client_id","string",packageName);if(id==0){toast("Add Firebase app/google-services.json first");return};val opt=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(id)).requestEmail().build();startActivityForResult(GoogleSignIn.getClient(this,opt).signInIntent,request)}
 @Deprecated("Legacy Google sign-in result") override fun onActivityResult(rc:Int,res:Int,data:Intent?){super.onActivityResult(rc,res,data);if(rc==request)try{val account=GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java);auth.signInWithCredential(GoogleAuthProvider.getCredential(account.idToken,null)).addOnCompleteListener{toast(if(it.isSuccessful)"Google sign-in successful" else "Sign-in failed")}}catch(e:Exception){toast("Google sign-in cancelled")}}
 private fun decode(s:String)=Html.fromHtml(s,Html.FROM_HTML_MODE_LEGACY).toString()
 private fun heading(s:String){text(s,26).gravity=Gravity.CENTER}
 private fun text(s:String,size:Int):TextView{val v=TextView(this).apply{text=s;textSize=size.toFloat();setTextColor(Color.DKGRAY);setPadding(4,12,4,12)};root.addView(v);return v}
 private fun button(s:String,act:()->Unit):Button{val b=Button(this).apply{text=s;isAllCaps=false;setOnClickListener{act()}};root.addView(b,LinearLayout.LayoutParams(-1,-2));return b}
 private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_LONG).show()
}