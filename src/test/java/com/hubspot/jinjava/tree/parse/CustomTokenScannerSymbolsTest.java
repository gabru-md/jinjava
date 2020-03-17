package com.hubspot.jinjava.tree.parse;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.lib.filter.JoinFilterTest.User;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;

public class CustomTokenScannerSymbolsTest {
  private Jinjava jinjava;
  private JinjavaConfig config;

  @Before
  public void setup() {
    config =
      JinjavaConfig.newBuilder().withTokenScannerSymbols(new CustomTokens()).build();
    jinjava = new Jinjava(config);
    jinjava.getGlobalContext().put("numbers", Lists.newArrayList(1L, 2L, 3L, 4L, 5L));
  }

  @Test
  public void testsThatCustomTokensDoesNotFail() {
    String template = "jinjava interpreter works correctly";
    assertThat(jinjava.render(template, new HashMap<String, Object>()))
      .isEqualTo(template);
  }

  @Test
  public void testsCustomTokensWithFilters() {
    assertThat(
        jinjava.render(
          "<% set d=d | default(\"some random value\") %><< d >>",
          new HashMap<>()
        )
      )
      .isEqualTo("some random value");
    assertThat(jinjava.render("<< [1, 2, 3, 3]|union(null) >>", new HashMap<>()))
      .isEqualTo("[1, 2, 3]");
    assertThat(jinjava.render("<< numbers|select('equalto', 3) >>", new HashMap<>()))
      .isEqualTo("[3]");
    assertThat(
        jinjava.render(
          "<< users|map(attribute='username')|join(', ') >>",
          ImmutableMap.of(
            "users",
            (Object) Lists.newArrayList(new User("foo"), new User("bar"))
          )
        )
      )
      .isEqualTo("foo, bar");
  }

  class CustomTokens extends TokenScannerSymbols {

    @Override
    public char getTokenPrefixChar() {
      return '<';
    }

    @Override
    public char getTokenPostfixChar() {
      return '>';
    }

    @Override
    public char getTokenFixedChar() {
      return 0;
    }

    @Override
    public char getTokenNoteChar() {
      return '#';
    }

    @Override
    public char getTokenTagChar() {
      return '%';
    }

    @Override
    public char getTokenExprStartChar() {
      return '<';
    }

    @Override
    public char getTokenExprEndChar() {
      return '>';
    }

    @Override
    public char getTokenNewlineChar() {
      return '\n';
    }

    @Override
    public char getTokenTrimChar() {
      return '-';
    }
  }
}