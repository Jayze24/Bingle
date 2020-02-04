package space.jay.bingle.data

import space.jay.bingle.Constants

data class Version(
    //변수 추가시 BoxVersion의 setVersion()에 같이 추가해 줘야 함!!
    var app: String = Constants.Init.APP, //서버에서 받아온 버전 담는데 사용. DB에 저장된건 무시해도 상관없음. 실제 비교는 서버에서 받아온 버전과 빌드 그래들에 있는 버전을 비교 함.
    var alert1: String = Constants.Init.STRING, //필수 업데이트 메세지
    var alert2: String = Constants.Init.STRING, //일반 업데이트 메세지
    var banner: String = Constants.Init.STRING
)
