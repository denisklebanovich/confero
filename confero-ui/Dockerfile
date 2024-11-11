# Use a Node.js base image for building the frontend
FROM node:21-alpine AS build

# Set the working directory
WORKDIR /app

COPY confero-ui/package*.json ./

# Copy openapi.yml
COPY confero-api/src/main/resources/openapi.yml ./src/generated/openapi.yml

# Install dependencies
RUN npm install

# Copy the rest of the application
COPY confero-ui .

# Build the app
RUN npm run build

# Use an nginx image to serve the frontend
FROM nginx:alpine

# Remove the default nginx index page
RUN rm -rf /usr/share/nginx/html/*

# Copy the build output to the nginx html directory
COPY --from=build /app/dist /usr/share/nginx/html

# Copy the nginx configuration file
COPY ./confero-ui/default.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
