package com.sliit.studentplatform.module1.service;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.common.response.PagedResponse;
import com.sliit.studentplatform.module1.dto.request.CreateGroupRequest;
import com.sliit.studentplatform.module1.dto.response.GroupResponse;
import com.sliit.studentplatform.module1.entity.GroupMember;
import com.sliit.studentplatform.module1.entity.ProjectGroup;
import com.sliit.studentplatform.module1.repository.GroupMemberRepository;
import com.sliit.studentplatform.module1.repository.ProjectGroupRepository;
import com.sliit.studentplatform.module1.service.impl.TeamServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TeamServiceImpl}.
 *
 * <p>
 * Uses JUnit 5 + Mockito. No Spring context is loaded — all dependencies are
 * mocked.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TeamServiceImpl Unit Tests")
class TeamServiceImplTest {

  @Mock
  private ProjectGroupRepository groupRepository;
  @Mock
  private GroupMemberRepository memberRepository;
  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private TeamServiceImpl teamService;

  private User creator;
  private CreateGroupRequest createRequest;

  @BeforeEach
  void setUp() {
    creator = User.builder()
        .id(1L)
        .fullName("Alice Smith")
        .email("alice@sliit.lk")
        .build();

    createRequest = CreateGroupRequest.builder()
        .name("Team Alpha")
        .description("Our awesome team")
        .maxMembers(4)
        .requiredSkills(new String[] { "Java", "Spring Boot" })
        .subject("SE3010")
        .build();
  }

  @Test
  @DisplayName("createGroup — should save group and add creator as leader")
  void createGroup_shouldSaveGroupAndAddCreatorAsLeader() {
    // Arrange
    ProjectGroup savedGroup = ProjectGroup.builder()
        .id(10L).name("Team Alpha").owner(creator).maxMembers(4).open(true).build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
    when(groupRepository.findByOwnerId(eq(1L), any(Pageable.class)))
        .thenReturn(new PageImpl<>(Collections.emptyList()));
    when(groupRepository.save(any(ProjectGroup.class))).thenReturn(savedGroup);
    when(memberRepository.save(any(GroupMember.class))).thenReturn(new GroupMember());
    when(memberRepository.countByGroupId(10L)).thenReturn(1);

    // Act
    GroupResponse response = teamService.createGroup(createRequest, 1L);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(10L);
    assertThat(response.getName()).isEqualTo("Team Alpha");
    assertThat(response.getOwnerName()).isEqualTo("Alice Smith");
    assertThat(response.getCurrentMembers()).isEqualTo(1);

    verify(groupRepository, times(1)).save(any(ProjectGroup.class));
    verify(memberRepository, times(1)).save(any(GroupMember.class));
  }

  @Test
  @DisplayName("createGroup — should throw ResourceNotFoundException when creator not found")
  void createGroup_shouldThrowWhenCreatorNotFound() {
    // Arrange
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> teamService.createGroup(createRequest, 99L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("User");

    verify(groupRepository, never()).save(any());
  }

  @Test
  @DisplayName("getGroupById — should return group when it exists")
  void getGroupById_shouldReturnGroupResponse() {
    // Arrange
    ProjectGroup group = ProjectGroup.builder()
        .id(5L).name("Beta Team").owner(creator).maxMembers(3).open(true).build();

    when(groupRepository.findById(5L)).thenReturn(Optional.of(group));
    when(memberRepository.countByGroupId(5L)).thenReturn(2);

    // Act
    GroupResponse response = teamService.getGroupById(5L);

    // Assert
    assertThat(response.getId()).isEqualTo(5L);
    assertThat(response.getCurrentMembers()).isEqualTo(2);
  }

  @Test
  @DisplayName("getGroupById — should throw ResourceNotFoundException when group not found")
  void getGroupById_shouldThrowWhenGroupNotFound() {
    when(groupRepository.findById(404L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> teamService.getGroupById(404L))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  @DisplayName("listOpenGroups — should return paged result")
  void listOpenGroups_shouldReturnPagedResult() {
    // Arrange
    ProjectGroup group = ProjectGroup.builder()
        .id(1L).name("Open Team").owner(creator).maxMembers(5).open(true).build();
    Page<ProjectGroup> page = new PageImpl<>(Collections.singletonList(group));

    when(groupRepository.findByOpenTrue(any(Pageable.class))).thenReturn(page);
    when(memberRepository.countByGroupId(1L)).thenReturn(1);

    // Act
    PagedResponse<GroupResponse> result = teamService.listOpenGroups(PageRequest.of(0, 10));

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getName()).isEqualTo("Open Team");
  }
}
