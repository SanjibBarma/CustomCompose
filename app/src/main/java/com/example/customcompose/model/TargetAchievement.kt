package com.example.customcompose.model

data class TargetAchievement(
    val id: Int,
    val user_id: Int,
    val locations: List<Location>,
    val products: List<Product>,
    val daily_target: Int,
    val over_achivement: Boolean,
    val additional_kpi: List<AdditionalKpi>,
    val daily_achievement: Int
)

data class Location(
    val id: Int,
    val name: String
)

data class Product(
    val id: Int,
    val name: String
)

data class AdditionalKpi(
    val identifier: String,
    val target: Int,
    val achievement: Int
)

const val TARGET_ACHIEVEMENT_LIST = """[{"id":1804,"user_id":24701,"locations":[{"id":88261,"name":"Alam Market, Gulshan-1"},{"id":88262,"name":"Alam Market, Gulshan-2"}],"products":[{"id":1,"name":"B&H SW"},{"id":2,"name":"B&H SF"},{"id":3,"name":"JPGL"},{"id":4,"name":"JPGL HD"},{"id":5,"name":"Marise"},{"id":6,"name":"Capstan"},{"id":7,"name":"Derby"},{"id":9,"name":"PallMall"},{"id":11,"name":"Royals Gold"},{"id":13,"name":"Star Market"},{"id":14,"name":"Wills Kings"},{"id":16,"name":"B&H BG"},{"id":17,"name":"B&H Platinum"},{"id":18,"name":"Alchemy Mix 1"},{"id":19,"name":"B&H Alchemy 7MG"},{"id":20,"name":"B&H Breeze"},{"id":21,"name":"B&H Fusion"},{"id":22,"name":"Lucky Strike Cool Crunch"},{"id":23,"name":"Lucky Strike Original"},{"id":24,"name":"Lucky Strike Supersonic"},{"id":25,"name":"Lucky Strike Big Chill"},{"id":26,"name":"Lucky Strike Fresh Twist"},{"id":27,"name":"Lucky Strike Red"},{"id":28,"name":"JP SW"},{"id":29,"name":"JP SP"},{"id":30,"name":"Royals LS"},{"id":31,"name":"Royals Next"},{"id":32,"name":"Derby Style"},{"id":33,"name":"Pilot"},{"id":34,"name":"Brand C"},{"id":35,"name":"Marlboro Advance"},{"id":36,"name":"Marlboro FF"},{"id":37,"name":"Marlboro Gold"},{"id":38,"name":"Marlboro Switch"},{"id":39,"name":"Derby Special"},{"id":40,"name":"Pilot LEP"},{"id":41,"name":"Hollywood LEP"},{"id":42,"name":"Derby LEP"},{"id":43,"name":"Sheikh 100s"},{"id":44,"name":"Sheikh FF"},{"id":45,"name":"Sheikh White"},{"id":46,"name":"K-2 FILTER KINGS"},{"id":47,"name":"Winston Blue"},{"id":48,"name":"Winston Red"},{"id":49,"name":"XSO"},{"id":50,"name":"Black FF"},{"id":51,"name":"Mond Switch"},{"id":52,"name":"Mond Lights"},{"id":53,"name":"Capital"},{"id":54,"name":"Pine SW"},{"id":55,"name":"Navy"},{"id":56,"name":"NAVY OPTION"},{"id":57,"name":"Oris FF"},{"id":58,"name":"More FF"},{"id":59,"name":"Oris Switch"},{"id":60,"name":"Dunhill FF"},{"id":61,"name":"Dunhill Switch"},{"id":62,"name":"Dunhill Lights"},{"id":63,"name":"Farstar"},{"id":64,"name":"Flag"},{"id":65,"name":"Guru"},{"id":66,"name":"LD Red"},{"id":67,"name":"L&M FF"},{"id":68,"name":"L&M SW"},{"id":69,"name":"Esse FF"},{"id":70,"name":"Esse Lights"},{"id":71,"name":"Esse SW"},{"id":72,"name":"Illicit"},{"id":73,"name":"Mond FF"},{"id":74,"name":"Castle"},{"id":75,"name":"Bon FF"},{"id":76,"name":"Others"},{"id":77,"name":"Up Trader"},{"id":78,"name":"Down trader"},{"id":79,"name":"Loyal"},{"id":80,"name":"Hollywood  "},{"id":81,"name":"Royals London Cut"},{"id":103,"name":"Black Diamond"},{"id":82,"name":"Brand C Dark Blue"},{"id":83,"name":"Brand C Purple Burst"},{"id":84,"name":"Brand C Blue Burst"},{"id":12,"name":"Star"},{"id":85,"name":"Star Next"},{"id":86,"name":"Star Switch"},{"id":87,"name":"Real"},{"id":88,"name":"9 No Maya Biri"},{"id":89,"name":"Akij Biri"},{"id":90,"name":"Other Biri Brand"},{"id":94,"name":"9 no Masud Biri"},{"id":95,"name":"Mala Biri"},{"id":97,"name":"Marlboro Black Advance"},{"id":98,"name":"Brand C Lemon Burst"},{"id":99,"name":"Premium ITB (SW)"},{"id":100,"name":"B&H Full Flavor"},{"id":101,"name":"Marlboro Red"},{"id":102,"name":"Sheikh Smooth"},{"id":104,"name":"Black Diamond Finecut"},{"id":105,"name":"Prime Gold"},{"id":106,"name":"LD Fresh"},{"id":107,"name":"Sunmoon"}],"additional_kpi_targets":null,"daily_target":20,"over_achivement":false,"additional_kpi":[],"daily_achievement":0}]"""