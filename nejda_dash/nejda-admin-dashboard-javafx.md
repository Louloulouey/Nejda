# Nejda Admin Dashboard Design Specification

## Overview
The Nejda Admin Dashboard will be a JavaFX desktop application designed for administrators to monitor and manage emergency alerts, user reports, and system configurations for the Nejda emergency detection and evaluation system at University Constantine 2.

## Design Principles
- **Consistency with Mobile App**: Maintain the same dark theme, color palette, and iconography style
- **Real-time Monitoring**: Dashboard layout optimized for real-time alert monitoring
- **Responsive Layout**: Properly scales with window size changes
- **Intuitive Navigation**: Clear, logical organization of features
- **Data Visualization**: Visual representation of emergency data and statistics

## Color Palette
- **Primary Background**: Dark gray (#1E1E1E)
- **Secondary Background**: Slightly lighter gray (#252525)
- **Accent Color**: Teal (#26A69A) - for buttons, indicators, and highlights
- **Alert Color**: Red (#F44336) - for emergency alerts
- **Text Colors**: White (#FFFFFF) and Light Gray (#B0B0B0)

## Main Dashboard Components

### 1. Navigation Sidebar (Left Side)
- University logo and "Nejda Admin" text at the top
- Navigation menu with the following items (including icons):
  - Dashboard (home icon)
  - Alerts Management (alert icon)
  - User Reports (document icon)
  - Evacuation Routes (map icon)
  - Safe Zones (location marker icon)
  - Emergency Contacts (phone icon)
  - System Settings (gear icon)
  - Analytics (chart icon)
  - User Management (people icon)
- Admin profile information at the bottom with logout option

### 2. Top Header Bar
- Current time and date
- Quick status indicators (system status, active alerts count)
- Search functionality
- Notifications bell icon
- Admin user profile icon/dropdown

### 3. Main Content Area (Center/Right)
Content changes based on selected navigation item, but the dashboard home includes:

#### Active Alerts Panel
- Real-time list of active emergency alerts
- Alert type, location, time, and status
- Ability to sort and filter alerts
- Action buttons for each alert (Resolve, Escalate, View Details)

#### Alert Distribution Map
- Campus map showing locations of active alerts
- Color-coded by severity and type
- Interactive - can click on locations for details

#### Emergency Response Status
- Currently deployed responders
- Response time metrics
- Resolution status for ongoing emergencies

#### Recent Activity Feed
- Chronological list of system activities
- User reports, alert triggers, and system actions
- Timestamp and brief description for each

#### Key Metrics Dashboard
- Total alerts in past 24 hours
- Average response time
- Resolution rate
- System performance indicators

### 4. Right Sidebar (Optional)
- Quick actions panel
- System status indicators
- Online staff/responders list

## Detailed Screen Designs

### Dashboard Home
- 4-6 key metric cards at the top (alerts today, active emergencies, avg. response time, etc.)
- Large alert monitoring table/grid below showing all active emergencies
- Campus map visualization showing alert locations
- Recent activity timeline at bottom

### Alerts Management Screen
- Tabbed interface with "Active", "Pending", "Resolved", and "All" alerts
- Detailed table with columns for:
  - Alert ID
  - Type (Fire, Medical, Security, etc.)
  - Location
  - Time Reported
  - Source (Sensor, User Report)
  - Status
  - Assigned Responders
  - Actions (View, Edit, Resolve, Delete)
- Filter and search capabilities
- Bulk action options

### User Reports Screen
- List of user-submitted emergency reports
- Status indicators (New, In Review, Verified, False Alarm)
- Detail panel showing report content, attached media, reporter info
- Ability to convert reports to official alerts
- Communication log with reporters

### Evacuation Routes Management
- Interactive campus map
- Tools to create/edit evacuation routes
- Route status indicators (clear, blocked, under maintenance)
- Evacuation zone assignment
- Route testing and simulation tools

### System Settings
- General system configuration
- Alert threshold settings
- Notification rules and channels
- Integration settings (with other campus systems)
- Backup and maintenance options
- User permission management

## Interactive Elements

### Alert Creation/Editing Modal
- Form to create new system alerts or edit existing ones
- Fields for:
  - Alert type (dropdown)
  - Location (building/room selector or map picker)
  - Description
  - Severity level
  - Attachment options
  - Distribution settings (who receives the alert)

### Response Coordination Panel
- Responder assignment interface
- Communication channels to deployed teams
- Resource allocation tools
- Status update mechanism

### Real-time Analytics Dashboard
- Historical alert frequency charts
- Response time trends
- Heat maps of emergency locations
- System performance metrics

## JavaFX Implementation Notes

### Layout Components
- Use BorderPane for main layout structure
- Implement navigation with JFXDrawer from JFoenix library
- TabPane for multi-tab interfaces
- TableView for data-heavy screens with custom cell factories
- ScrollPane for content areas exceeding the viewport

### Special Components
- Use WebView for map visualizations (integrating with OpenStreetMap or Google Maps)
- Charts from JavaFX Charts package for analytics
- Custom notifications using PopOver library
- Real-time updates with background services and tasks

### Animation and Transitions
- Subtle animations for state changes
- Alert notifications with fade-in effects
- Smooth transitions between screens
- Progress indicators for loading operations

### Responsive Considerations
- Anchor content to scale with window size
- Use percentage-based layouts where appropriate
- Min/max size constraints for critical components
- Dynamic font sizing

## Technical Implementation Guidelines

### Architecture
- Model-View-Controller (MVC) pattern
- Service layer for business logic
- Data access layer for database operations
- WebSocket or long polling for real-time updates

### Key JavaFX Features to Utilize
- Property binding for real-time UI updates
- CSS styling for consistent look and feel
- FXML for layout definition
- Custom controls for specialized functionality

### Third-party Libraries
- JFoenix for material design components
- ControlsFX for additional UI controls
- FontAwesomeFX for iconography
- Ikonli for additional icon packs
- Apache POI for report generation
- Log4j for logging

## Accessibility Considerations
- Keyboard navigation support
- Screen reader compatibility
- Color contrast adherence
- Text scaling support
- Focus indicators for interactive elements

## Recommendations for Development Approach
1. Start with the basic application shell and navigation
2. Implement the dashboard home view with mock data
3. Build alert management functionality
4. Add reporting and analytics features
5. Implement settings and configuration screens
6. Connect to backend services and replace mock data
7. Add real-time update capabilities
8. Polish UI and improve performance
9. Add export and reporting features
10. Implement user management and access controls
