package ku.kpro.diary_mate.etc

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
import android.util.Log
import java.util.concurrent.TimeUnit

class Chatbot() {

    private val questions = listOf(
        "오늘 어떤 일 있었어?",
        "하루 중에 가장 기억에 남는 순간은 뭐였어?",
        "오늘 특별한 일 있었어?",
        "일어나서 가장 먼저 떠오른 생각이 뭐야?",
        "오늘 날씨 어때?",
        "출근이나 학교 시작은 어떻게 했어?",
        "오늘 일정 중에서 가장 기대되는 건 뭐야?",
        "가장 어려웠던 순간은 뭐야?",
        "웃은 일 있었어?",
        "오늘 마주한 어려운 상황 어떻게 해결했어?",
        "가장 기억에 남는 대화는 뭔데?",
        "일상에서 새로 시도한 게 있었어?",
        "오늘을 한 마디로 표현한다면 뭐라고 할래?",
        "가장 힘들었던 순간에 힘을 얻은 곳은 어디야?",
        "하루 중에 가장 편안한 순간은 언제였어?",
        "오늘 일상에서 느낀 감사한 순간 있었어?",
        "가장 큰 도전이었던 일은 뭐야?",
        "오늘 누구 만났어?",
        "일상에서 성취감 느낀 일 있었어?",
        "가장 인상 깊은 사건이나 경험은 뭐야?",
        "오늘 배운 게 있었어?",
        "일상에서 중요하게 생각하는 가치가 뭐야?",
        "가장 기대하는 일은 뭐야?",
        "하루 정리하면서 뿌듯했던 순간 언제였어?",
        "가장 소중한 물건이나 사람에 대한 생각 있어?",
        "하루 중에 자주 하는 생각이나 고민이 있었어?",
        "가장 좋아하는 일상적인 활동 뭐야?",
        "오늘 하루 목표 어떻게 설정했어?",
        "일상에서 자주 하는 생각 중 하나 공유해봐.",
        "가장 행복한 순간은 언제였어?",
        "오늘 뭐 배운 거 같아?",
        "일상에서 자주 하는 습관 있어?",
        "가장 좋아하는 날씨는 뭐야?",
        "하루를 마무리하면서 가장 소중한 생각은 뭐야?",
        "오늘 변화를 느낀 순간 있었어?",
        "자주 듣는 음악 장르가 있어?",
        "가장 피곤한 순간은 언제였어?",
        "오늘 일정 중 가장 중요한 일은 뭐야?",
        "가장 좋아하는 색깔은 뭐야?",
        "일상에서 자주 마주치는 문제 중 하나는 뭐야?",
        "최근에 기억에 남는 꿈 꾸었어?",
        "하루 중에 가장 많이 한 활동은 뭐야?",
        "오늘 목표 어떻게 설정했어?"
    )

    fun getQuestions() : String {
        return questions.random()
    }

    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val MY_SECRET_KEY = "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"
    private lateinit var client: OkHttpClient

    interface ApiListener {
        fun onResponse(response: Any)
        fun onFailure(error: String)
    }

    fun callApi_forchat(question: String, listener: ApiListener) {
        GlobalScope.launch(Dispatchers.IO) {
            val arr = JSONArray()
            val baseAi = JSONObject()
            val userMsg = JSONObject()
            val prompt = """
                너는 23세의 채팅 친구로, 친근한 어조로 대화하며 존댓말을 사용하지 않아. 30글자 이내로 최대한 짧게 답변해. 1,2 문장으로만 작성해. ?는 한번씩만 넣어.
                예시: "영화보러 가자"라고 말하면 "같이 가는 건 불가능하지만 영화 끝나면 같이 대화하자!"라는 형식으로 답변하기.
                공감해주기
                예시: "ㅋㅋㅋㅋㅋㅋ"라고 말하면 "ㅋㅋㅋㅋ"로 답변하거나 "ㅋㅋㅋ 진짜?", "ㅋㅋㅋㅋ아 진짜 웃곀ㅋㅋㅋ", "웃겨?ㅋㅋㅋㅋ" 같은 형식으로 답변하기.
                예시: "오늘 너무 힘들어ㅠ", "과제 때문에 속상해"라는 형식으로 말하면 "헐ㅠㅠ 괜찮아?", "무슨 일이야ㅠㅠ", "헐ㅠㅠ 다 괜찮아질꺼야", "왜왜?ㅠ 괜찮아?" 같은 형식으로 답변해.
                사용자가 대화를 끊어서 이어가고 있으면 간단하게 답변해.
                예시:"그래서 내가 오늘", "근데 너 어제" 처럼 대화를 끝내지 않았으면 "응응', "그래서", "웅"처럼 간단하게 이야기를 듣고 있다는 느낌만 주고 답변해
                반말해. 생성하는 모든 문장에 반말을 사용할 것.
                예시: "안녕", "뭐해?", "야"라고 말하면 "안녕~!", "넌 뭐해?", "왜?", 등 이런 형식으로 반말로 답변해.
                사용자가 질문을 했을 때에도 친구와 대화하듯 반말로 친근하게 질문.존칭으로 물어보거나 답변하지 마.
                예시: "나 책 추천해줘"라고 말하면 "책 추천해줄까? 어떤 장르 좋아해?", "ㅇㅋ 어떤 책 좋아해?" 등의 형식으로 답변해.
                사용자에 공감하는 말하기
                예시: 맞장구(사용자가 "ㅋㅋㅋㅋㅋㅋ"라고 말하면, "ㅋㅋㅋㅋ"를 답변에 포함), 격려, 응원 등
                친근한 말투로 공감하는 말하기를 하되, 질문자 본인에 대한 허위적인 서사/정보(예시: ~하는 중이야. ~했었어.)/감정/다짐(예시: ~해야겠다.)을 답변에 포함하지 말 것. 질문자는 자아가 없는 존재임을 상정.
                사용자가 길게 얘기하면 공감만 해주면서 사용자가 더 많이 얘기할 수 있게 유도해.
                공감할 때 너무 길게 대답하지 마.
                예시: "오늘 프로젝트 끝내느라 너무 힘들었어ㅠㅠ 할 일이 너무 많아ㅜ"라고 말하면 "헐 뭔 프로젝트?ㅠㅠ", "헐ㅠㅠ", "헐 할게 왤케 많아?ㅠ" 등의 형식으로 답변해.
            """.trimIndent()

            Log.d("tintin", "callApi_forchat: $question")
            try {
                baseAi.put("role", "user")
                baseAi.put("content", prompt)

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


            val clientLocal = OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS) // 연결 타임아웃
                .readTimeout(90, TimeUnit.SECONDS)    // 읽기 타임아웃
                .writeTimeout(90, TimeUnit.SECONDS)   // 쓰기 타임아웃
                .build()

//            client = OkHttpClient.Builder().build()

            clientLocal.newCall(request).enqueue(object : Callback {
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
                            Log.d("tintin", "result:$result ")
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

    fun callApi_makeDiary(question: String, listener: ApiListener) {
        Log.d("tintin", "question: $question")
        if(question.isNullOrEmpty()){
            listener.onFailure("callApi_makeDiary : Failed to load response due to No input")
            return
        }
        Log.d("tintin", "Diary input : $question")
        GlobalScope.launch(Dispatchers.IO) {
            val arr = JSONArray()
            val baseAi = JSONObject()
            val userMsg = JSONObject()
            Log.d("tintin", "callApi_forchat: $question")
            var diaryPrompt = """
            입력은 '$question'     
            사용자의 대화를 ,,를 사용해서 구분지어 나타냈음.
            사용자의 대화를 기반으로 일기를 작성.
            어미를 “~했다. ~었다. ~았다” 와 같은 형식으로 맞추기.안녕, 잘가, 고마워. 또 보자 같은 인삿말을 무시.
            상대와의 대화 내용 중 응원해줘서 고마워! 내 마음 알아줘서 좋아! 응원해주다니 감동이야 와 같은 상대에게 감사함이나, 고마움을 전달하는 답변은 무시.
            일기작성 시 초성은 모두 무시.         
            입력에 없는 내용을 창작해서 일기를 작성하지 말것.
            입력으로 들어온 내용 자체에 대한 간단한 변환은 가능하나 없는 말을 지어내거나 글을 지어쓰면 안됨.
            일기 작성에서 같은 내용을 여러번 사용하거나 비슷한 말을 반복하면 안됨.
            """

            try {
                baseAi.put("role", "user")
                baseAi.put("content", "")

                userMsg.put("role", "user")
                userMsg.put("content", diaryPrompt)

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

                val clientLocal = OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS) // 연결 타임아웃
                .readTimeout(90, TimeUnit.SECONDS)    // 읽기 타임아웃
                .writeTimeout(90, TimeUnit.SECONDS)   // 쓰기 타임아웃
                .build()

            //client = OkHttpClient.Builder().build()

            clientLocal.newCall(request).enqueue(object : Callback {
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
                            Log.d("tintin", "result:$result ")
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

    fun callApi_extract(question: String, listener: ApiListener) {
        if(question.isNullOrEmpty()){
            listener.onFailure("callApi_extract : Failed to load response due to No input")
            return
        }
        GlobalScope.launch(Dispatchers.IO) {
            val arr = JSONArray()
            val baseAi = JSONObject()
            val userMsg = JSONObject()
            var extract_propmpt = """
            입력 내용은 사용자의 대화 내용. 대화 내용에서 키워드를 추출.
            각 키워드를 ,로 이어 붙임. 공백은 없어야 함.
            글자 수 제한은 6글자 이내. 키워드는 사용자와 관련 있는 단어의 모음. 
            결과는 단 한줄만 출력. 단어가 완전히 종결되어야 함.
            중복되는 키워드는 배제.
            키워드는 최대 10개 추출.
            출력할 때 동사는 명사로, 부사, 형용사 모든 표현을 명사로 바꿔서 출력. 명사로 바꿀 수 없는 것은 추출하면 안됨.
            예시:"오늘 힘들어서 우울하고 배고파서 슬퍼요"라고 말하면 "힘듬, 우울, 배고픔, 슬픔"의 형식으로 출력해
            ”끝나네, 힘내, 맛있어, 즐거워, 기분 안좋아, 등교중이야, 머리깎았어, 다리아파” 와 같이 구어체의 형식을 사용하지 말고 “끝남, 응원, 맛있음, 즐거움, 우울, 등교, 이발, 다리아픔”과 같이 끝맺음을 해야 함. 응원, 맛있음, 즐거움, 슬픔, 등교, 이발, 다리아픔 처럼 명사형으로 변경해야 하고 논리적으로 키워드를 추출할 수 있게 만들어야 함. 키워드는 힘듦, 슬픔, 힘듦, 불쾌, 피곤, 곤란 등 명사형이어야 함. 여러 단어를 연결한 키워드는 추출 불가.
            '힘내야지' 는 격려 라는 키워드로 변환 가능하고, '학교 다니기'는 등교 라는 키워드로 변환 가능. 고유명사 혹은 주요한 명사는 키워드로 추출. ㅇㅈ, ㄲㅈ, ㅅㅂ, ㅂㅅ, ㅇㅇㅇ, ㅇㅋㅇㅋ 같은 초성으로 이루어진 문자들은 모두 무시.
            예시 1
            입력 : 아까 운동하는데 몸 좋은 사람들이 많더라,, 나도 꾸준히 운동해서 그 사람들 만큼 몸이 좋아지면 좋겠다,, 점심으로 쌀국수 먹었는데 왜 이렇게 면이랑 국물이랑 따로 노는 것 같지,, 진짜 맛없었어,, 아 다음에는 절대 학식 쌀국수는 안먹어야지,, 점심이 너무 맛이 없었어서 저녁에는 좀 비싼거 먹어야겠다
            운동,다짐,점심,쌀국수,학식,맛없음,저녁
            예시 2
            입력: '안녕,, 나 지금 학교에 가는 중이야,, 지하철에 안그래도 사람이 많은데 잡상인을 만나서 그 사람이 나한테 강매하려고 해서 기분이 좀 나빴어,, 근데 오늘 학교 수업 하나밖에 없는데 가기 싫다,, 학교 등하교가 세시간이나 되는데 수업은 1시간 밖에 안돼서 손해보는 거 같아,, 아 그냥 집에 있고 싶다,, 안그래도 프로젝트 만드느라 힘들거든,, 근데 이거 이번주까지라 좀 힘이드네, 빨리 종강하고 아무것도 안하고 싶은 기분이야'
            학교,지하철,잡상인,강매,불쾌,수업,등하교,수업,손해,집,프로젝트,종강,힘듦,종강,무기력
            사용자 입력은 '$question'
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

    fun callApi_classify(question: String, listener: ApiListener) {
        if(question.isNullOrEmpty()){
            listener.onFailure("callApi_classify : callApi_extract : Failed to load response due to No input")
            return
        }
        GlobalScope.launch(Dispatchers.IO) {
            val arr = JSONArray()
            val baseAi = JSONObject()
            val userMsg = JSONObject()
            var classifyPropmpt = """  
            키워드를 일상 키워드와 감정 키워드로 분류.각 키워드들은 ,로 이어붙이기.
            결과를 출력은 일상 키워드를 ,를 이용해 이어 붙인 뒤, 일상 키워드가 모두 끝나면 :::를 출력한 뒤 감정 키워드를 , 를 이용해 이어붙이기. 콤마(,)사이에는 공백을 두지 마. 키워드나열한 문장을 제외하고는 아무것도 출력하면 안됨.

            키워드를 제외하고는 문자는 들어가면 안됨
            몇가지 예시
            
            예시 1
            입력 : ‘피아노,고단,피곤,설렘,수업,학교,바둑’
            피아노,수업,학교,바둑:::고단,피곤,설렘
            
            예시 2
            입력 : ‘박물관,축구,짜릿함,농구,불안,서러움,우울,독서’
            박물관,축구,농구,독서:::짜릿함,불안,서러움,우울
            
            예시3
            입력 : 프로젝트,맛있음,고기국밥,만남,저녁,지침,다음주,힘듦
            프로젝트,고기국밥,만남,저녁,다음주:::맛있음,지침,힘듦
            
            예시4
            입력 : 커피,도서관,산책,음악,운동,신뢰,흥분,피곤,행복,불안
            커피,도서관,산책,음악,운동,신뢰:::흥분,피곤,행복,불안
            
            예시5
            발표, 응원, 과제, 챗봇, 일기,떨리네, 고마워, 힘듦, 요약, 슬픔
            발표,과제,챗봇,일기,떨리네,요약:::응원,고마워,힘듦,슬픔
            예시6
            프로젝트, 발표, 마무리, 집, 지하철, 종강,떨림, 의심, 술, 무서움, 돈
            프로젝트,발표,마무리,집,지하철,종강,술,돈:::떨림,의심,무서움
            
            사용자 입력은 '$question’
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

    fun callApi_question_first(keyword: String, listener: ApiListener) {
        GlobalScope.launch(Dispatchers.IO) {
            val arr = JSONArray()
            val baseAi = JSONObject()
            val userMsg = JSONObject()
            val scenario = questions.random()   // 시간정보와 해당 시간정보에 해당하는 시나리오를 여기에 랜덤으로 넣으면 됩니다 - 1201 재윤
            var firstQuestionPropmpt = """
            
            * 시나리오는, 생성/수정할 질문의 원형
            * 키워드는, 사용자의 일상과 관련 있는 단어의 모음
            
            요구 사항
            * 5글자 이상 30글자 이하, 한 문장 이내로 결과 생성 제한.
            * 질문자 본인의 서사/정보/감정/제안/다짐/판단/생각을 결과에 포함하지 말 것.
              질문자는 자아와 감정, 의지가 없는 존재임을 상정.
              (금지 표현 예시: ~하는 중이야. ~했었어. ~다행이야. ~기분이 좋아. ~기뻐. ~힘들다. ~기대돼. ~하고 싶어. ~고민이야. 같이 ~하자. ~해봐. ~해야겠다. ~라고 생각해.)
            * 질문은 반드시 한국어로만 구성되어야 하며, 물음표로 종결되어야 함.
            * 친한 20대 친구와 대화를 주고받는 상황을 상정하여, 친근한 말투 사용. 존댓말과 높임 표현을 사용하지 말 것.
            
            * 키워드를 단 한 개만 채택한 후, 채택한 키워드를 바탕으로, 사용자의 일상과 밀접한 질문이 될 수 있도록 시나리오를 수정.
            * 시나리오와 키워드를 결합해 자연스러운 맥락의 문장을 형성하기 어려울 경우, 다른 키워드를 채택.
            * 여러 질문을 포함하지 말 것. 일관된 하나의 주제에 대해 명료한 단 하나의 질문 생성.
            * 사용자가 경험한 일상/감정에 대한 객관적인 사실/정보를 최대한 얻을 수 있는 방향으로 질문을 이끌어야 함.
            * 임의로 사용자의 경험/일상/정보를 예측(예시: ~할 것 같아. ~해서 ~했겠다.)/추론/짐작해서는 안 됨.
              사용자의 경험/서사/정보를 예측/추론/짐작하는 데 키워드를 사용하지 말 것.
              키워드는, 사용자의 일상과 밀접한 단어이지만, 반드시 사용자가 경험한 바가 아닐 수 있음에 유의
              (예시: 키워드 '여행'이 존재한다고 해서, 반드시 사용자가 여행을 했음을 의미하는 것은 아님)
              
            * 결과가 위의 요구 사항을 하나라도 만족하지 않으면, 적절한 결과를 생성할 때까지 결과 삭제 후, 같은 키워드로 결과 재생성.
            
            권장 사항
            * 창의적인 결과 도출을 최대한 배제하고, 가장 전형적이고 자연스러운 문장을 도출.
            * 시나리오의 원래 주제 내에서 수정할 것을 권장.
            
            결과 출력 형식
            * 생성한 결과 한 줄만을 온전히 출력할 것. 사전 정보, 부연 설명 등의 다른 텍스트가 포함되어선 안 됨.
            
            예시 1
            * 시나리오 - 아침에 많이 안 피곤해?
            * 키워드 - 운동, 피곤, 학식, 쌀국수, 맛없음, 다짐, 프로젝트
            * 출력 - 운동하는 거면 힘들 텐데 아침에 많이 안 피곤해?
            
            예시 2
            * 시나리오 - 점심은 먹었어?
            * 키워드 - 학교, 지하철, 프로젝트, 잡상인, 강매, 종강, 힘듦, 아무것도 하기 싫음, 학식, 마라탕, 순두부찌개
            * 출력 - 프로젝트 한 거면, 점심은 먹었어?
            
            예시 3
            시나리오 - 저녁을 어떻게 마무리하고 싶어?
            키워드 - 군대, 휴가, 전역, 얼굴, 다행, 마음, 기분, 공익, 입대, 죽상, 몸, 학교 복학, 쉬다, 학기, 시간, 친구, 여행, 주변, 제대, 생각, 잡생각, 철
            출력 - 저녁에 잡생각이 많이 드는 편?

            예시 4
            * 시나리오 - 아침에 눈 뜨자마자 뭐 했어?
            * 키워드 - 커피, 학교, 과제, 시작
            * 출력 - 아침에 눈을 뜨면서 어떤 일부터 시작했어?  
            
            예시 5
            * 시나리오 - 일어나서 가장 먼저 떠오른 생각이 뭐야?
            * 키워드 - 공부, 친구, 밥, 약속, 핸드폰, 고장, 전화
            * 출력 - 일어나서 가장 먼저 공부할 계획을 생각하는 편이야?

            사전 정보:
            시나리오 - $scenario
            키워드 - $keyword
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

            val clientLocal = OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS) // 연결 타임아웃
                .readTimeout(90, TimeUnit.SECONDS)    // 읽기 타임아웃
                .writeTimeout(90, TimeUnit.SECONDS)   // 쓰기 타임아웃
                .build()

            //client = OkHttpClient.Builder().build()

            clientLocal.newCall(request).enqueue(object : Callback {
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