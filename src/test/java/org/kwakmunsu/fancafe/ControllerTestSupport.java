package org.kwakmunsu.fancafe;

import org.kwakmunsu.fancafe.global.security.TestSecurityConfig;
import org.kwakmunsu.fancafe.member.application.MemberFacade;
import org.kwakmunsu.fancafe.member.presentation.MemberController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.json.JsonMapper;

@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = {
        MemberController.class,
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvcTester mvcTester;

    @Autowired
    protected JsonMapper jsonMapper;

    @MockitoBean
    protected MemberFacade memberFacade;

}