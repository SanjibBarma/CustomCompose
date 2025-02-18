package com.example.customcompose.model

import kotlinx.serialization.Serializable

@Serializable
data class SurveyDataModel(
    val type: String,
    val group: String,
    val blocks: List<Block>,
    val jumping_logic: List<JumpingLogic>
)

@Serializable
data class Block(val id: String, val skip: Skip?, val type: String, val options: List<Option>?, val referTo: ReferTo?, val question: Question?, val required: String?, val validations: Validations?)

@Serializable
data class Skip(val id: String, val group_no: String)

@Serializable
data class Option(val value: String, val referTo: ReferTo?)

@Serializable
data class ReferTo(val id: String?, val group_no: String?)

@Serializable
data class Question(val slug: String, val alias: String)

@Serializable
data class Validations(val regex: String?, val prefix: String?, val max: Int?, val min: Int?, val terms: List<String>?, val bypass: Boolean?, val device: Boolean?, val server: Boolean?)

@Serializable
data class JumpingLogic(val id: String, val group_no: String, val conditions: List<String>)

const val JSON_STRING ="""[{"type":"referring","group":"7","blocks":[{"id":"100","skip":{"id":"-1","group_no":"7"},"type":"audio_start","options":null,"referTo":{"id":"3","group_no":"1"},"question":{"slug":"Recording Call","alias":"audio"}}],"jumping_logic":[]},{"type":"numbervalidation","group":"1","blocks":[{"id":"3","type":"contactNo","options":null,"question":{"slug":"Contact Number","alias":"contact_no"},"required":"true","validations":{"regex":"(^(\\+88|0088)?(01){1}[3456789]{1}(\\d){8})${'$'}","prefix":"+880"}},{"id":"4","type":"date","options":null,"question":{"slug":"Date of Birth","alias":"dob"},"required":"true","validations":{"max":35,"min":18}}],"jumping_logic":[{"id":"5","group_no":"2","conditions":[]}]},{"type":"referring","group":"2","blocks":[{"id":"5","skip":{"id":"-1","group_no":"2"},"type":"textInput","options":null,"referTo":{"id":"7","group_no":"2"},"question":{"slug":"Name","alias":"name"},"required":"true","validations":{"regex":"^[a-zA-Z ]{3,}${'$'}"}},{"id":"7","skip":{"id":"-1","group_no":"2"},"type":"textInput","options":null,"referTo":{"id":"8","group_no":"2"},"question":{"slug":"Address","alias":"address"},"required":"true"},{"id":"8","skip":{"id":"-1","group_no":"2"},"type":"terms","options":null,"referTo":{"id":"submit","group_no":"submit"},"question":{"slug":"Terms & Conditions","alias":"signature"},"validations":{"terms":["শর্তাবলী","আমি নিশ্চিত করছি যে -","","১. আমার বয়স ১৮ বছরের অধিক এবং আমি স্বেচ্ছায়, সজ্ঞানে ও সুস্থ শরীরে, অন্যের বিনা প্ররোচনায় অত্র স্বাক্ষর প্রদান করিলাম","২. আমি নিশ্চিত করিতেছি যে উল্লেখিত তথ্যগুলি সঠিক ও সত্য","৩. আমি EU বা অন্য কোন বহিরাগত দেশের নাগরিক নই","৪. আমি পরবর্তী তিন বছরের জন্য আমার তথ্য সংরক্ষণ/প্রক্রিয়া/স্থানান্তর করার জন্য অনুমোদন ও সম্মতি প্রদান করলাম","৫. আমি স্ব-ইচ্ছায় তথ্যসমূহ প্রদান করলাম","৬. আমি নিশ্চিত করিতেছি যে তথ্য প্রদানের ব্যাপারে আমাকে কোন ধরনের অর্থপ্রদান/অন্য কোনভাবে প্ররোচনা/প্রভাবিত করা হয়নি","৭.  আমি নিশ্চিত করছি যে, প্রশিক্ষণ এবং ভবিষ্যৎ অনুসন্ধানের জন্য আমার কথোপকথন টি রেকর্ড করা যেতে পারে","৮. আমি নিশ্চিত করিতেছি যে প্রদত্ত তথ্যের ব্যাপারে ভবিষ্যতে,  বাংলাদেশ আইন অনুযায়ী, কোনো আদালত বা কর্তৃপক্ষের সামনে কোন ধরনের আইনগত অভিযোগ মূলক কর্মকান্ড গ্রহণ করিব না","৯. বর্তমান কথোপকথন বিষয়ে ভবিষ্যতে যোগাযোগের জন্য স্বেচ্ছায় অনুমতি প্রদান করছি।“,উপরোক্ত শর্তাবলী মেনে আমার তথ্য প্রদান করছি","","উপরোক্ত শর্তাবলী মেনে আমার তথ্য প্রদান করছি","I Agree to Sign Digitally"]}}],"jumping_logic":[{"id":"submit","group_no":"submit","conditions":[]}]},{"type":"referring","group":"8","blocks":[{"id":"101","skip":{"id":"-1","group_no":"8"},"type":"audio_end","options":null,"referTo":{"id":"submit","group_no":"submit"},"question":{"slug":"Recording Call","alias":"audio"}}],"jumping_logic":[]}]""";
