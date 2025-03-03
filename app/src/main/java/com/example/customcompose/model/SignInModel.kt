package com.example.customcompose.model

data class SignInModel(
    val status: String,
    val data: LoginData,
)

data class LoginData(
    val token: String?,
    val id: Int?,
    val username: String?,
    val user_type: String?,
    val server_time: String?,
    val reset_stts: Boolean?,
    val lowest_ff: Boolean?,
    val authorize: Boolean?,
    val block: Boolean?,
    val agency_id: Int?,
    val org_id: Int?,
    val tag: String?,
    val theme: ThemeOrg?,
    val dailyCheckObj: CheckInOutData?,
    val access_list: List<Int>?,
    val forcedLogout: Boolean,
    val org_settings: OrgSettings?,
    val app_version: AppVersion?,
    val reportto_id: Int?
)

data class ThemeOrg(
    val priamry_color: String?,
    val org_logo: String?
)

data class CheckInOutData(
    var checkInStatus: Boolean?,
    var checkInTime: String?,
    var checkInDate: String?,
    var checkOutStatus: Boolean?,
    var checkOutTime: String?,
    var checkOutDate: String?
)

data class OrgSettings(
    var FF_Module: FFModule?,
    var leave: LeaveModule?,
    var attendance: AttendanceModule?
)

data class FFModule(
    var employment_type: List<EmploymentType?>?
)

data class EmploymentType(
    var id: Int?,
    var name: String?
)

data class LeaveModule(
    var weekends: List<Int?>?,
    var sick_leave: String?,
    var casual_leave: String?,
    var start_of_the_year: Int?
)

data class AttendanceModule(
    var Attendance: Attendance?
)

data class Attendance(
    var checkIn_time: String?,
    var checkOut_time: String?
)

data class AppVersion(
    var version: String?,
    var force: Boolean?,
    var url: String?,
    var id: Int?,
    var md5: String?
)

