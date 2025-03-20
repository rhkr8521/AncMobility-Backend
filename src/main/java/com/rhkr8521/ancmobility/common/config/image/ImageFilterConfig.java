package com.rhkr8521.ancmobility.common.config.image;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageFilterConfig {

    @Bean
    public FilterRegistrationBean<ImageHotlinkingPreventionFilter> imageHotlinkingPreventionFilter() {
        FilterRegistrationBean<ImageHotlinkingPreventionFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ImageHotlinkingPreventionFilter());
        registrationBean.addUrlPatterns("/api/images/*");

        return registrationBean;
    }
}