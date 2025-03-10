package com.example.customcompose.model

data class CampaignsModel(
    val status: String,
    val message: String,
    val data: List<CampaignListModel>,
    var selectedCamp: Int = 0
)

data class CampaignListModel(
    val id: Int,
    val name: String,
    val joint_call: Boolean,
    val joint_call_target: JointCallTarget,
    val cluster_mapping: ClusterMapping,
    val call_checkback_target: CallCheckbackTarget,
    val campaign_type: String
)

data class JointCallTarget(
    val perFFjointCallCount: Int,
    val totalJointCallTarget: Int,
    val totalTargetValidation: Boolean,
)

data class ClusterMapping(
    val max_slot: Int,
    val multiFFassignment: Boolean
)

data class CallCheckbackTarget(
    val target: Int,
    val status: Boolean
)