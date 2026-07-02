package org.kwakmunsu.fancafe.community.post.application;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.fancafe.community.post.application.dto.NewPost;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostFacade {

    private final PostCommandService postCommandService;

    public void register(NewPost newPost, Long authorId) {
        postCommandService.register(newPost, authorId);
    }

}