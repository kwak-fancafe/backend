package org.kwakmunsu.fancafe.fixture;

import org.kwakmunsu.fancafe.community.category.domain.Category;
import org.kwakmunsu.fancafe.community.post.domain.Post;
import org.kwakmunsu.fancafe.community.post.domain.PostStatus;

public class PostFixture {

    public static final String TITLE = "테스트 게시글";
    public static final String CONTENT = "테스트 내용";

    public static Post draft() {
        return Post.create(TITLE, CONTENT, PostStatus.DRAFT, MemberFixture.member(), category());
    }

    public static Post published() {
        return Post.create(TITLE, CONTENT, PostStatus.PUBLIC, MemberFixture.member(), category());
    }

    public static Post hidden() {
        Post post = published();
        post.hide();
        return post;
    }

    public static Category category() {
        try {
            var ctor = Category.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
