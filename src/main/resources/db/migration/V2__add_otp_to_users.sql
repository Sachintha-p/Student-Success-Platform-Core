-- Migration to add OTP fields for SLIIT email login
ALTER TABLE users ADD COLUMN otp VARCHAR(6);
ALTER TABLE users ADD COLUMN otp_expiry TIMESTAMP;