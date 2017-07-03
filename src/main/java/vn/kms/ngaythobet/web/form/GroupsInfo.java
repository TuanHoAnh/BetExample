package vn.kms.ngaythobet.web.form;

import lombok.Data;

@Data
public class GroupsInfo {

    Integer totalGroupsCount;

    Integer publishedGroupsCount;

    Integer draftGroupsCount;

    Integer pendingGroupsCount;
}
