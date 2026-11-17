plugins { alias(libs.plugins.android.application); alias(libs.plugins.kotlin.android); alias(libs.plugins.ksp); alias(libs.plugins.google.services) }
android { namespace="com.quizscan.app"; compileSdk=36
 defaultConfig { applicationId="com.quizscan.app"; minSdk=23; targetSdk=35; versionCode=1; versionName="1.0" }
 compileOptions { sourceCompatibility=JavaVersion.VERSION_17; targetCompatibility=JavaVersion.VERSION_17 }
 kotlinOptions { jvmTarget="17" }
}
dependencies {
 implementation(libs.androidx.core.ktx); implementation(libs.androidx.appcompat); implementation(libs.google.material)
 implementation(libs.androidx.activity.ktx); implementation(libs.androidx.recyclerview)
 implementation(libs.androidx.room.runtime); implementation(libs.androidx.room.ktx); ksp(libs.androidx.room.compiler)
 implementation(libs.retrofit); implementation(libs.retrofit.gson); implementation(libs.gson)
 implementation(libs.kotlinx.coroutines.android)
 implementation(platform(libs.firebase.bom)); implementation(libs.firebase.auth)
 implementation(libs.play.services.auth); implementation(libs.play.services.code.scanner)
 testImplementation(libs.junit)
}