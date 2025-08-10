package com.hackathon.junglegym.domain.qeustion.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class FilenameParser {

  private static final Pattern P =
      Pattern.compile("^(.+?)\\(([^)]+)\\)\\((제\\d+호)\\)\\((\\d{8})\\)\\.pdf$");

  public Parsed parse(String filename) {
    if (filename == null) return null;
    Matcher m = P.matcher(filename);
    if (!m.find()) {
      String base = filename.replaceAll("\\.pdf$", "");
      return new Parsed(base, null, null, "current");
    }

    String lawName = m.group(1);
    String lawType = m.group(2);
    String promulgationNo = m.group(3);
    String date = m.group(4);
    String revisionId = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")).toString();

    return new Parsed(lawName, lawType, promulgationNo, revisionId);
  }

  public record Parsed(String lawName, String lawType, String promulgationNo, String revisionId) {}
}
