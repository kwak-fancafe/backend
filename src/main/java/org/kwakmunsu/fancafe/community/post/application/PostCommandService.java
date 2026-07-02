package org.kwakmunsu.fancafe.community.post.application;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.kwakmunsu.fancafe.community.category.domain.Category;
import org.kwakmunsu.fancafe.community.category.infrastructure.CategoryJpaRepository;
import org.kwakmunsu.fancafe.community.post.application.dto.NewPost;
import org.kwakmunsu.fancafe.community.post.domain.Post;
import org.kwakmunsu.fancafe.community.post.infrastructure.PostJpaRepository;
import org.kwakmunsu.fancafe.global.support.EntityStatus;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;
import org.kwakmunsu.fancafe.member.application.MemberServiceHelper;
import org.kwakmunsu.fancafe.member.domain.Member;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostCommandService {

    private static final Safelist TITLE_SAFELIST = Safelist.none();
    private static final Safelist CONTENT_SAFELIST = Safelist.relaxed();

    private final PostJpaRepository postJpaRepository;
    private final MemberServiceHelper memberServiceHelper;
    private final CategoryJpaRepository categoryJpaRepository;

    public void register(NewPost newPost, Long authorId) {
        Member author = memberServiceHelper.find(authorId);
        author.validateWritePermission();

        Category category = categoryJpaRepository.findByIdAndStatus(newPost.categoryId(), EntityStatus.ACTIVE)
                .orElseThrow(() -> new CoreException(ErrorType.CATEGORY_NOT_FOUND));

        postJpaRepository.save(Post.create(
                Jsoup.clean(newPost.title(), TITLE_SAFELIST),
                Jsoup.clean(newPost.content(), CONTENT_SAFELIST),
                newPost.status(),
                author,
                category
        ));
    }

}