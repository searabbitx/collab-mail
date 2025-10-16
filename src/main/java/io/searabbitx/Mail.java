package io.searabbitx;

import java.util.concurrent.atomic.AtomicReference;

record Mail(String from, String to, String subject, String content) {
    static Builder builder() {
        return new Builder();
    }
    static class Builder {
        private String body;
        private String from;
        private String to;

        private Builder() {
            body = null;
        }

        Mail build() {
            return new Mail(from, to, null, body);
        }

        Builder withBody(String body) {
            this.body = body;
            return this;
        }

        Builder withFrom(String from) {
            this.from = from;
            return this;
        }

        public Builder withTo(String to) {
            this.to = to;
            return this;
        }
    }
}
