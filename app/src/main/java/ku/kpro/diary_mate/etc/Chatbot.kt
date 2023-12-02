package ku.kpro.diary_mate.etc

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ku.kpro.diary_mate.R
import ku.kpro.diary_mate.activity.MainActivity
import ku.kpro.diary_mate.fragment.ChattingFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import android.os.Handler
import android.os.Looper

class Chatbot() {

    private val questions = listOf(
        "오늘 어떤 일이 있었나요?",
        "하루 중 가장 기억에 남는 순간은 무엇이었나요?",
        "오늘 특별한 일이 있었나요?",
        // TODO: ChatGPT로 생성한 질문 리스트로 대체
    )

    fun getQuestions() : String {
        return questions.random()
    }
    private var listener: ApiListener?=null

    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val MY_SECRET_KEY = "노션에서 꺼내 쓰시면 됩니다."
    private lateinit var client: OkHttpClient

    interface ApiListener {
        fun onResponse(response: Any)
        fun onFailure(error: String)
    }

    fun setOnapiListener(listener :ApiListener ){
        this.listener = listener
    }
    fun callApi_forchat(question: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val arr = JSONArray()
            val baseAi = JSONObject()
            val userMsg = JSONObject()

            try {
                baseAi.put("role", "user")
                baseAi.put("content", "You are a 23years old friend who talks in a friendly way without honorifics.")

                userMsg.put("role", "user")
                userMsg.put("content", question)

                arr.put(baseAi)
                arr.put(userMsg)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val json = JSONObject()

            try {
                json.put("model", "gpt-3.5-turbo")
                json.put("messages", arr)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val body = json.toString().toRequestBody(JSON)

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer $MY_SECRET_KEY")
                .post(body)
                .build()

            client = OkHttpClient.Builder().build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Handler(Looper.getMainLooper()).post {
                        listener?.onFailure("Failed to load response due to ${e.message}")
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        try {
                            val jsonObject = JSONObject(response.body!!.string())
                            val jsonArray = jsonObject.getJSONArray("choices")
                            val result =
                                jsonArray.getJSONObject(0).getJSONObject("message").getString("content")
                            Handler(Looper.getMainLooper()).post {
                                //통신이 성공하면 역기서 결과만 추출하여 건네주네
                                listener?.onResponse(result.trim())
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        Handler(Looper.getMainLooper()).post {
                            listener?.onFailure("Failed to load response due to ${response.body!!.string()}")
                        }
                    }
                }
            })
        }
    }

    fun callApi_summarize(question: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val arr = JSONArray()
            val baseAi = JSONObject()
            val userMsg = JSONObject()
            var dairy_propmpt = question +"\n+위의 사용자의 대화를 기반으로 일기를 작성해줘\n" + """
            일기의 예시를 들어줄게
            오늘은 종강한 첫 날이다. 지금까지 과제하고 또 시험 준비하다가 이렇게 갑자기 아무것도 할게 없어지니 되게 기분이 이상하다. 그래도 지금까지 꽤 힘들게 시험
            준비를 해서 그런지 되게 후련하고 안도감이 많이든다.
            방학이 2달인데 그래도 이 시간 알차게 사용하려면 이렇게 퍼져있는 시간이 오래 가면 안될 듯 하니 이제 방학동안 무얼 할지 계획을 세우고 이행할 스케쥴을 짜야겠다.
            아 맞다, 그리고 돈도 벌어야 환다. 다음 학기때 어차피 아르바이트 안할거니 이번 방학때 열심히 알바 해서 돈이나 땡겨야지.
            알바가 힘들긴 하겠지만 뭐 과제랑 시험 준비하는 것보다는 쉬울 것 같아 걱정은 안된다.
            또 다른 일기 예시를 줄게
            군대에서 휴가 나온 친구를 만나고 왔다. 3개월 뒤 전역이라 얼굴이 그래도 좀 좋아 보여서 다행이라고 생각했다.
            난 몸이 안좋아 공익이라 군대는 모르겠지만 입대하는 친구들이 다 죽상이라 가끔 가다보면 마음이 좀 안좋았는데 전역이 코앞이니 나도 좀 기분이 나아진다.
            이제 바로 전역하고 학교 복학은 안하고 좀 쉬려는 모양이다. 나도 학기중에 시간 되면 친구랑 여행이나 가고싶다. 주변 친구들이 얼른 제대해서 예전처럼 아무생각 없이 다 같이 여행이나 다니면 좋겠다.
            이놈들이랑 있으면 그래도 잡생각은 안들어서 좋다. 그래도 철 좀 들었으면 하는 마음이다"""

            try {
                baseAi.put("role", "user")
                baseAi.put("content", "너는 23살 대학생이고 오늘 있었던 일을 바탕으로 일기를 작성하는 역할이야.")

                userMsg.put("role", "user")
                userMsg.put("content", dairy_propmpt)

                arr.put(baseAi)
                arr.put(userMsg)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val json = JSONObject()

            try {
                json.put("model", "gpt-3.5-turbo")
                json.put("messages", arr)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val body = json.toString().toRequestBody(JSON)

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer $MY_SECRET_KEY")
                .post(body)
                .build()

            client = OkHttpClient.Builder().build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Handler(Looper.getMainLooper()).post {
                        listener?.onFailure("Failed to load response due to ${e.message}")
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        try {
                            val jsonObject = JSONObject(response.body!!.string())
                            val jsonArray = jsonObject.getJSONArray("choices")
                            val result =
                                jsonArray.getJSONObject(0).getJSONObject("message").getString("content")
                            Handler(Looper.getMainLooper()).post {
                                //통신이 성공하면 역기서 결과만 추출하여 건네주네
                                listener?.onResponse(result.trim())
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        Handler(Looper.getMainLooper()).post {
                            listener?.onFailure("Failed to load response due to ${response.body!!.string()}")
                        }
                    }
                }

            })
        }
    }

    fun callApi_extract(question: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val arr = JSONArray()
            val baseAi = JSONObject()
            val userMsg = JSONObject()
            var extract_propmpt = """
            대화의 내용을 줄 테니 해당 대화 내용을 바탕으로 위와 같이 주요 키워드를 추출해줘.
            $question
            키워드 추출의 예시를 들어줄게
            아까 운동하는데 몸 좋은 사람들이 많더라,, 나도 꾸준히 운동해서 그 사람들 만큼 몸이 좋아지면 좋겠다.
            점심으로 쌀국수 먹었는데 왜 이렇게 면이랑 국물이랑 따로 노는 것 같지.
            진짜 맛없었어,, 아 다음에는 절대 학식 쌀국수는 안먹어야지. 점심이 너무 맛이 없었어서 저녁에는 좀 비싼거 먹어야겠다
            해당 내용에서 주요 키워드는 운동, 다짐, 점심, 쌀국수, 학식, 맛없음, 저녁 이야.
            """

            try {
                baseAi.put("role", "user")
                baseAi.put("content", "해당 내용에서 중요하게 생각한 키워드를 뽑아내는 역할이야.")

                userMsg.put("role", "user")
                userMsg.put("content", extract_propmpt)

                arr.put(baseAi)
                arr.put(userMsg)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val json = JSONObject()

            try {
                json.put("model", "gpt-3.5-turbo")
                json.put("messages", arr)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val body = json.toString().toRequestBody(JSON)

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer $MY_SECRET_KEY")
                .post(body)
                .build()

            client = OkHttpClient.Builder().build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Handler(Looper.getMainLooper()).post {
                        listener?.onFailure("Failed to load response due to ${e.message}")
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        try {
                            val jsonObject = JSONObject(response.body!!.string())
                            val jsonArray = jsonObject.getJSONArray("choices")
                            val result =
                                jsonArray.getJSONObject(0).getJSONObject("message").getString("content")
                            Handler(Looper.getMainLooper()).post {
                                //통신이 성공하면 역기서 결과만 추출하여 건네주네
                                listener?.onResponse(result.trim())
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        Handler(Looper.getMainLooper()).post {
                            listener?.onFailure("Failed to load response due to ${response.body!!.string()}")
                        }
                    }
                }

            })
        }
    }

    fun callApi_classify(question: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val arr = JSONArray()
            val baseAi = JSONObject()
            val userMsg = JSONObject()
            var classifyPropmpt = """  
            추출된 키워드를 줄게. 키워드를 일상 키워드와 감정 키워드로 분류해줘.각 키워드들은  ,로 이어붙여
            $question
            """

            try {
                baseAi.put("role", "user")
                baseAi.put("content", "입력받은 내용으로 키워드를 일상 키워드와 감정 키워드로 분류해.")

                userMsg.put("role", "user")
                userMsg.put("content", classifyPropmpt)

                arr.put(baseAi)
                arr.put(userMsg)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val json = JSONObject()

            try {
                json.put("model", "gpt-3.5-turbo")
                json.put("messages", arr)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val body = json.toString().toRequestBody(JSON)

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer $MY_SECRET_KEY")
                .post(body)
                .build()

            client = OkHttpClient.Builder().build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Handler(Looper.getMainLooper()).post {
                        listener?.onFailure("Failed to load response due to ${e.message}")
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        try {
                            val jsonObject = JSONObject(response.body!!.string())
                            val jsonArray = jsonObject.getJSONArray("choices")
                            val result =
                                jsonArray.getJSONObject(0).getJSONObject("message").getString("content")
                            Handler(Looper.getMainLooper()).post {
                                //통신이 성공하면 역기서 결과만 추출하여 건네주네
                                listener?.onResponse(result.trim())
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        Handler(Looper.getMainLooper()).post {
                            listener?.onFailure("Failed to load response due to ${response.body!!.string()}")
                        }
                    }
                }

            })
        }
    }

    fun callApi_question_first(question: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val arr = JSONArray()
            val baseAi = JSONObject()
            val userMsg = JSONObject()
            val scenario = "아침에 많이 안피곤해?"   // 시간정보와 해당 시간정보에 해당하는 시나리오를 여기에 랜덤으로 넣으면 됩니다 - 1201 재윤
            var firstQuestionPropmpt = """
            시나리오는, 생성할 질문의 원형
            키워드는, 사용자의 일상에 관한 키워드

            사전 정보로 주어진 키워드 중, 사전 정보의 시나리오의 문맥과 가장 어울리는 것을 한 개만 선택할 것.
            선택한 키워드 기반으로, 사용자의 일상에 대해 묻는 질문이 될 수 있도록, 시나리오를 한두 줄 이내로 적절히 수정.
            이때 수정된 시나리오는 문장 안에서 자연스러운 맥락을 형성해야 하며, 논리 전개가 이상하거나 비문이 되어서는 안 됨.
            친한 20대 친구와 주고받는 대화임을 고려하여, 친근한 말투 사용.
            질문자의 정보가 시나리오 수정에 반영되어서는 안 됨.
            출력에는, 사전 정보 또는 선택한 키워드 등의 다른 정보가 일절 포함되어선 안 되며,
            수정된 시나리오 한 줄만을 온전히 출력할 것

            질문 생성 과정 예시:
            예시 1 
            시나리오 - 아침에 많이 안 피곤해?
            키워드 - 운동, 피곤, 학식, 쌀국수, 맛없음, 다짐, 프로젝트
            출력 -
            운동도 하고 프로젝트도 하면서 힘들었을 텐데 아침에 많이 안 피곤해?
            예시 2
            시나리오 -  점심은 먹었어?
            키워드 - 학교, 지하철, 프로젝트, 잡상인, 강매, 종강, 힘듦, 아무것도 하기 싫음, 학식, 마라탕, 순두부찌개
            출력 - 점심은 맛있는거 먹었어? 학교에서 프로젝트 때문에 되게 힘들었을텐데 맛있는거 먹고 기운 내보자.
            예시 3
            시나리오 - 저녁을 어떻게 마무리하고 싶어?
            키워드 - 군대, 휴가, 전역, 얼굴, 다행, 마음, 기분, 공익, 입대, 죽상, 몸, 학교 복학, 쉬다, 학기, 시간, 친구, 여행, 주변, 제대, 생각, 잡생각, 철
            출력 - 저녁에 잡생각이 많이 들어서 시간을 많이 쓰는거야? 그러지 말고 주변을 둘러보며 좋게 저녁을 마무리 할 수 있지 않을까?
            사전 정보:
            시나리오 - $scenario
            키워드 - `$question
            출력 -
            """
            try {
                baseAi.put("role", "user")
                baseAi.put("content", "너는 23살의 대학생이고 친근하고 공감을 잘 해주는 성격이야. 동갑의 대학생 친구에게 존댓말은 하지 않고 질문을 하는 역할이야")

                userMsg.put("role", "user")
                userMsg.put("content", firstQuestionPropmpt)

                arr.put(baseAi)
                arr.put(userMsg)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val json = JSONObject()

            try {
                json.put("model", "gpt-3.5-turbo")
                json.put("messages", arr)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val body = json.toString().toRequestBody(JSON)

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer $MY_SECRET_KEY")
                .post(body)
                .build()

            client = OkHttpClient.Builder().build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Handler(Looper.getMainLooper()).post {
                        listener?.onFailure("Failed to load response due to ${e.message}")
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        try {
                            val jsonObject = JSONObject(response.body!!.string())
                            val jsonArray = jsonObject.getJSONArray("choices")
                            val result =
                                jsonArray.getJSONObject(0).getJSONObject("message").getString("content")
                            Handler(Looper.getMainLooper()).post {
                                //통신이 성공하면 역기서 결과만 추출하여 건네주네
                                listener?.onResponse(result.trim())
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        Handler(Looper.getMainLooper()).post {
                            listener?.onFailure("Failed to load response due to ${response.body!!.string()}")
                        }
                    }
                }
            })
        }
    }
}


/*

private lateinit var openAiApiHandler: OpenAiApiHandler

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    openAiApiHandler = OpenAiApiHandler(object : OpenAiApiHandler.ApiListener {
        override fun onResponse(response: String) {
            addResponse(response)
        }

        override fun onFailure(error: String) {
            addResponse(error)
        }
    })

    btn_send.setOnClickListener {
        val question = et_msg.text.toString().trim()
        addToChat(question, Message.SENT_BY_ME)
        et_msg.text.clear()
        hideSoftKeyboard()
        openAiApiHandler.callApi(question)
    }

*/