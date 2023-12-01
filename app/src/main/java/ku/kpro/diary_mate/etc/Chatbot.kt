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
                userMsg.put("content", "")

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
            var classify_propmpt = """
            추출된 키워드를 줄게. 키워드를 일상 키워드와 감정 키워드로 분류해줘.각 키워드들은  ,로 이어붙여
            $question
            """

            try {
                baseAi.put("role", "user")
                baseAi.put("content", "입력받은 내용으로 키워드를 일상 키워드와 감정 키워드로 분류해.")

                userMsg.put("role", "user")
                userMsg.put("content", classify_propmpt)

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
            var classify_propmpt = """
            친구에게 할 질문의 시나리오는 "$scenario" 이고,
            어제 대화 키워드는 $question 이고 이 키워드 중 시나리오와 어울리는 키워드를 뽑아 시나리오와 연결지어 사용자에게 질문을 해줘.
            예시를 하나 들어줄게. 만약 시나리오가 "점심은 먹었어?" 이고, 어제 키워드는 학교, 지하철, 프로젝트, 잡상인, 강매, 종강, 힘듦, 아무것도 하기 싫음, 학식이고 
            이 키워드 중 시나리오와 어울리는 키워드를 뽑아 사용자에게 할 질문은 "오늘도 점심으로 학식을 먹을거야? "
            결과는 질문 단 한문장만 출력해줘. 오늘도 점심으로 학식을 먹을거야? 처럼 한 문장만 출력해.
            """

            try {
                baseAi.put("role", "user")
                baseAi.put("content", "너는 채팅으로 너의 친한 20대 친구에게 질문하는 역할이야.")

                userMsg.put("role", "user")
                userMsg.put("content", classify_propmpt)

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