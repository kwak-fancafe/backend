package org.kwakmunsu.fancafe.community.post.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.fancafe.UnitTestSupport;
import org.kwakmunsu.fancafe.fixture.PostFixture;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;

class PostTest extends UnitTestSupport {

    // ---- create ----

    @Test
    void 게시글_임시저장으로_생성_성공() {
        var post = PostFixture.draft();

        assertThat(post.getPostStatus()).isEqualTo(PostStatus.DRAFT);
    }

    @Test
    void 게시글_즉시_발행으로_생성_성공() {
        var post = PostFixture.published();

        assertThat(post.getPostStatus()).isEqualTo(PostStatus.PUBLIC);
    }

    @Test
    void 게시글_생성_실패_제목_200자_초과() {
        String longTitle = "가".repeat(201);

        assertThatThrownBy(() -> Post.create(longTitle, PostFixture.CONTENT, PostStatus.DRAFT, null, null))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.POST_INVALID_TITLE.getMessage());
    }

    @Test
    void 게시글_생성_실패_내용_50000자_초과() {
        String longContent = "a".repeat(50_001);

        assertThatThrownBy(() -> Post.create(PostFixture.TITLE, longContent, PostStatus.DRAFT, null, null))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.POST_INVALID_CONTENT.getMessage());
    }

    // ---- saveDraft ----

    @Test
    void 임시저장_성공() {
        var post = PostFixture.published();

        post.saveDraft();

        assertThat(post.getPostStatus()).isEqualTo(PostStatus.DRAFT);
    }

    // ---- publish ----

    @Test
    void 게시글_발행_성공_DRAFT_상태() {
        var post = PostFixture.draft();

        post.publish();

        assertThat(post.getPostStatus()).isEqualTo(PostStatus.PUBLIC);
    }

    @Test
    void 게시글_발행_성공_PRIVATE_상태() {
        var post = PostFixture.hidden();

        post.publish();

        assertThat(post.getPostStatus()).isEqualTo(PostStatus.PUBLIC);
    }

    @Test
    void 게시글_발행_실패_이미_PUBLIC_상태() {
        var post = PostFixture.published();

        assertThatThrownBy(post::publish)
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.POST_CANNOT_PUBLISH.getMessage());
    }

    // ---- hide ----

    @Test
    void 게시글_비공개_전환_성공() {
        var post = PostFixture.published();

        post.hide();

        assertThat(post.getPostStatus()).isEqualTo(PostStatus.PRIVATE);
    }

    @Test
    void 게시글_비공개_전환_실패_DRAFT_상태() {
        var post = PostFixture.draft();

        assertThatThrownBy(post::hide)
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.POST_CANNOT_HIDE.getMessage());
    }

    @Test
    void 게시글_비공개_전환_실패_이미_PRIVATE_상태() {
        var post = PostFixture.hidden();

        assertThatThrownBy(post::hide)
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.POST_CANNOT_HIDE.getMessage());
    }

    // ---- update ----

    @Test
    void 게시글_수정_성공() {
        var post = PostFixture.published();
        var newCategory = PostFixture.category();

        post.update("수정된 제목", "수정된 내용", newCategory);

        assertThat(post).extracting(
                Post::getTitle,
                Post::getContent,
                Post::getCategory
        ).containsExactly(
                "수정된 제목",
                "수정된 내용",
                newCategory
        );
    }

    @Test
    void 게시글_수정_실패_내용_50000자_초과() {
        var post = PostFixture.published();
        String longContent = "a".repeat(50_001);

        assertThatThrownBy(() -> post.update(PostFixture.TITLE, longContent, PostFixture.category()))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.POST_INVALID_CONTENT.getMessage());
    }

}
