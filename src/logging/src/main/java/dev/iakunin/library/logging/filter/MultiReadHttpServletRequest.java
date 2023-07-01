package dev.iakunin.library.logging.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;

public final class MultiReadHttpServletRequest extends HttpServletRequestWrapper {

    private ByteArrayOutputStream cachedBytes;

    public MultiReadHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (cachedBytes == null) {
            cacheInputStream();
        }

        return new CachedServletInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(
            new InputStreamReader(getInputStream())
        );
    }

    private void cacheInputStream() throws IOException {
        cachedBytes = new ByteArrayOutputStream();
        IOUtils.copy(super.getInputStream(), cachedBytes);
    }

    public final class CachedServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream input;

        public CachedServletInputStream() {
            super();
            this.input = new ByteArrayInputStream(
                MultiReadHttpServletRequest.this.cachedBytes.toByteArray()
            );
        }

        @Override
        public boolean isFinished() {
            return input.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public int read() {
            return input.read();
        }
    }
}
