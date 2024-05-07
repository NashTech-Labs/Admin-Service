package com.nashtech.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {GCPConfig.class})
@ExtendWith(SpringExtension.class)
class GCPConfigTest {
    @Autowired
    private GCPConfig gCPConfig;

    @Test
    void testCanEqual() {
        // Arrange, Act and Assert
        assertFalse(gCPConfig.canEqual("Other"));
        assertTrue(gCPConfig.canEqual(gCPConfig));
    }

    @Test
    void testEquals() {
        // Arrange, Act and Assert
        assertNotEquals(new GCPConfig(), null);
        assertNotEquals(new GCPConfig(), "Different type to GCPConfig");
        assertNotEquals(new GCPConfig(), 1);
    }

    @Test
    void testEquals2() {
        // Arrange
        GCPConfig gcpConfig = new GCPConfig();
        gcpConfig.setGcpProjectId("myproject");

        // Act and Assert
        assertNotEquals(gcpConfig, new GCPConfig());
    }

    @Test
    void testEquals3() {
        // Arrange
        GCPConfig gcpConfig = new GCPConfig();
        gcpConfig.setGcpBucketName("bucket-name");

        // Act and Assert
        assertNotEquals(gcpConfig, new GCPConfig());
    }

    @Test
    void testEquals4() {
        // Arrange
        GCPConfig gcpConfig = new GCPConfig();
        gcpConfig.setGcpTopicId("42");

        // Act and Assert
        assertNotEquals(gcpConfig, new GCPConfig());
    }

    @Test
    void testEquals5() {
        // Arrange
        GCPConfig gcpConfig = new GCPConfig();

        GCPConfig gcpConfig2 = new GCPConfig();
        gcpConfig2.setGcpProjectId("myproject");

        // Act and Assert
        assertNotEquals(gcpConfig, gcpConfig2);
    }

    @Test
    void testEquals6() {
        // Arrange
        GCPConfig gcpConfig = new GCPConfig();

        GCPConfig gcpConfig2 = new GCPConfig();
        gcpConfig2.setGcpBucketName("bucket-name");

        // Act and Assert
        assertNotEquals(gcpConfig, gcpConfig2);
    }

    @Test
    void testEquals7() {
        // Arrange
        GCPConfig gcpConfig = new GCPConfig();

        GCPConfig gcpConfig2 = new GCPConfig();
        gcpConfig2.setGcpTopicId("42");

        // Act and Assert
        assertNotEquals(gcpConfig, gcpConfig2);
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        GCPConfig gcpConfig = new GCPConfig();

        // Act and Assert
        assertEquals(gcpConfig, gcpConfig);
        int expectedHashCodeResult = gcpConfig.hashCode();
        assertEquals(expectedHashCodeResult, gcpConfig.hashCode());
    }

    @Test
    void testEqualsAndHashCode2() {
        // Arrange
        GCPConfig gcpConfig = new GCPConfig();
        GCPConfig gcpConfig2 = new GCPConfig();

        // Act and Assert
        assertEquals(gcpConfig, gcpConfig2);
        int expectedHashCodeResult = gcpConfig.hashCode();
        assertEquals(expectedHashCodeResult, gcpConfig2.hashCode());
    }

    @Test
    void testEqualsAndHashCode3() {
        // Arrange
        GCPConfig gcpConfig = new GCPConfig();
        gcpConfig.setGcpProjectId("myproject");

        GCPConfig gcpConfig2 = new GCPConfig();
        gcpConfig2.setGcpProjectId("myproject");

        // Act and Assert
        assertEquals(gcpConfig, gcpConfig2);
        int expectedHashCodeResult = gcpConfig.hashCode();
        assertEquals(expectedHashCodeResult, gcpConfig2.hashCode());
    }

    @Test
    void testEqualsAndHashCode4() {
        // Arrange
        GCPConfig gcpConfig = new GCPConfig();
        gcpConfig.setGcpBucketName("bucket-name");

        GCPConfig gcpConfig2 = new GCPConfig();
        gcpConfig2.setGcpBucketName("bucket-name");

        // Act and Assert
        assertEquals(gcpConfig, gcpConfig2);
        int expectedHashCodeResult = gcpConfig.hashCode();
        assertEquals(expectedHashCodeResult, gcpConfig2.hashCode());
    }

    @Test
    void testEqualsAndHashCode5() {
        // Arrange
        GCPConfig gcpConfig = new GCPConfig();
        gcpConfig.setGcpTopicId("42");

        GCPConfig gcpConfig2 = new GCPConfig();
        gcpConfig2.setGcpTopicId("42");

        // Act and Assert
        assertEquals(gcpConfig, gcpConfig2);
        int expectedHashCodeResult = gcpConfig.hashCode();
        assertEquals(expectedHashCodeResult, gcpConfig2.hashCode());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        GCPConfig gcpConfig = new GCPConfig();

        // Act
        gcpConfig.setGcpBucketName("bucket-name");
        gcpConfig.setGcpProjectId("myproject");
        gcpConfig.setGcpTopicId("42");
        String actualToStringResult = gcpConfig.toString();
        String actualGcpBucketName = gcpConfig.getGcpBucketName();
        String actualGcpProjectId = gcpConfig.getGcpProjectId();

        // Assert that nothing has changed
        assertEquals("42", gcpConfig.getGcpTopicId());
        assertEquals("GCPConfig(gcpProjectId=myproject, gcpBucketName=bucket-name, gcpTopicId=42)", actualToStringResult);
        assertEquals("bucket-name", actualGcpBucketName);
        assertEquals("myproject", actualGcpProjectId);
    }
}
