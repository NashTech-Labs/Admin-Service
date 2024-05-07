package com.nashtech.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@ContextConfiguration(classes = {CorsConfig.class})
@ExtendWith(SpringExtension.class)
class CorsConfigTest {
    @Autowired
    private CorsConfig corsConfig;

    @Test
    void testAddCorsMappings() {
        // Arrange
        CorsRegistry registry = mock(CorsRegistry.class);
        when(registry.addMapping(Mockito.<String>any())).thenReturn(new CorsRegistration("Path Pattern"));

        // Act
        corsConfig.addCorsMappings(registry);

        // Assert
        verify(registry).addMapping(Mockito.<String>any());
    }
}
