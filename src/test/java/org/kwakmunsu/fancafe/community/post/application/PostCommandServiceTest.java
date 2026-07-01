package org.kwakmunsu.fancafe.community.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.fancafe.UnitTestSupport;
import org.kwakmunsu.fancafe.community.category.infrastructure.CategoryJpaRepository;
import org.kwakmunsu.fancafe.community.post.application.dto.NewPost;
import org.kwakmunsu.fancafe.community.post.domain.Post;
import org.kwakmunsu.fancafe.community.post.domain.PostStatus;
import org.kwakmunsu.fancafe.community.post.infrastructure.PostJpaRepository;
import org.kwakmunsu.fancafe.fixture.MemberFixture;
import org.kwakmunsu.fancafe.fixture.PostFixture;
import org.kwakmunsu.fancafe.global.support.EntityStatus;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;
import org.kwakmunsu.fancafe.member.application.MemberServiceHelper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PostCommandServiceTest extends UnitTestSupport {

    @Mock
    PostJpaRepository postJpaRepository;

    @Mock
    MemberServiceHelper memberServiceHelper;

    @Mock
    CategoryJpaRepository categoryJpaRepository;

    @InjectMocks
    PostCommandService postCommandService;

    @Test
    void 게시물_등록_성공_CREATOR() {
        var newPost = newPost("제목", "내용");

        given(memberServiceHelper.find(1L)).willReturn(MemberFixture.creatorMember());
        given(categoryJpaRepository.findByIdAndStatus(1L, EntityStatus.ACTIVE))
                .willReturn(Optional.of(PostFixture.category()));

        postCommandService.register(newPost, 1L);

        verify(postJpaRepository).save(any(Post.class));
    }

    @Test
    void 게시물_등록_성공_MANAGER() {
        var newPost = newPost("제목", "내용");

        given(memberServiceHelper.find(1L)).willReturn(MemberFixture.managerMember());
        given(categoryJpaRepository.findByIdAndStatus(1L, EntityStatus.ACTIVE))
                .willReturn(Optional.of(PostFixture.category()));

        postCommandService.register(newPost, 1L);

        verify(postJpaRepository).save(any(Post.class));
    }

    @Test
    void 게시물_등록_성공_XSS_제거() {
        var newPost = newPost("제목", "<p>내용</p><script>alert('xss')</script>");

        given(memberServiceHelper.find(1L)).willReturn(MemberFixture.creatorMember());
        given(categoryJpaRepository.findByIdAndStatus(1L, EntityStatus.ACTIVE))
                .willReturn(Optional.of(PostFixture.category()));

        postCommandService.register(newPost, 1L);

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postJpaRepository).save(captor.capture());

        assertThat(captor.getValue().getContent())
                .contains("내용")
                .doesNotContain("<script>");
    }

    @Test
    void FAN_일경우_게시물_등록이_제한된다() {
        var newPost = newPost("제목", "내용");

        given(memberServiceHelper.find(1L)).willReturn(MemberFixture.member());

        assertThatThrownBy(() -> postCommandService.register(newPost, 1L))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_CANNOT_WRITE_POST.getMessage());
    }

    @Test
    void 존재하지않는_카테고리일경우_게시물_등록_실패() {
        var newPost = newPost("제목", "내용");

        given(memberServiceHelper.find(1L)).willReturn(MemberFixture.creatorMember());
        given(categoryJpaRepository.findByIdAndStatus(1L, EntityStatus.ACTIVE))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> postCommandService.register(newPost, 1L))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.CATEGORY_NOT_FOUND.getMessage());
    }

    @Test
    void 존재하지않는_회원일경우_게시물_등록_실패() {
        var newPost = newPost("제목", "내용");

        given(memberServiceHelper.find(1L))
                .willThrow(new CoreException(ErrorType.MEMBER_NOT_FOUND));

        assertThatThrownBy(() -> postCommandService.register(newPost, 1L))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_NOT_FOUND.getMessage());
    }

    private NewPost newPost(String title, String content) {
        return NewPost.builder()
                .categoryId(1L)
                .title(title)
                .content(content)
                .status(PostStatus.PUBLIC)
                .build();
    }

}
