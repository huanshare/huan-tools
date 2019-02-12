package com.huanshare.tools.logback.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.huanshare.tools.utils.StringUtils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class HuanshareThresholdFilter extends Filter<ILoggingEvent> {

    private String levels;
    private List<String> levelList = new ArrayList<>();

    public void setLevels(String levels) {
        this.levels = levels;
        if (StringUtils.isNotBlank(levels)) {
            levelList = Arrays.asList(StringUtils.split(levels, ";"));
        }
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        if (levelList.contains(event.getLevel().levelStr)) {
            return FilterReply.NEUTRAL;
        } else {
            return FilterReply.DENY;
        }
    }

    @Override
    public void start() {
        if (StringUtils.isNotBlank(levels)) {
            super.start();
        }
    }
}
